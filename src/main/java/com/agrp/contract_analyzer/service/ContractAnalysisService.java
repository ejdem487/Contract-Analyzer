package com.agrp.contract_analyzer.service;

import com.agrp.contract_analyzer.dto.*;
import com.agrp.contract_analyzer.model.ContractAnalysis;
import com.agrp.contract_analyzer.model.LegalIssue;
import com.agrp.contract_analyzer.repository.ContractAnalysisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ContractAnalysisService {


    private final GeminiService geminiService;
    private final LawSourceService lawSourceService;
    private final ContractAnalysisRepository repository;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ContractAnalysisService(GeminiService geminiService,
                                   LawSourceService lawSourceService,
                                   ContractAnalysisRepository repository,
                                   PromptBuilder promptBuilder,
                                   ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.lawSourceService = lawSourceService;
        this.repository = repository;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    public ContractAnalysisResponse analyze(ContractAnalysisRequest request) {


        LawIdentificationResult identification = identifyLaws(
                request.originalContractText(),
                request.revisedContractText()
        );


        String lawContext = buildLawContext(identification);


        String response = geminiService.generateStructured(
                promptBuilder.buildAnalysisPrompt(
                        request.originalContractText(),
                        request.revisedContractText(),
                        identification.contractType(),
                        identification.keyTopics(),
                        lawContext
                ),
                ANALYSIS_SCHEMA
        );

        try {
            AnalysisResult result = objectMapper.readValue(response, AnalysisResult.class);
            return saveAndReturn(request, identification, result);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při parsování AI analýzy: " + e.getMessage(), e);
        }
    }

    private LawIdentificationResult identifyLaws(String original, String revised) {
        String prompt = promptBuilder.buildIdentificationPrompt(original, revised);
        String response = geminiService.generateStructured(prompt, LAW_IDENTIFICATION_SCHEMA);

        try {
            return objectMapper.readValue(response, LawIdentificationResult.class);
        } catch (Exception e) {
            return new LawIdentificationResult("neznámá", List.of(), List.of());
        }
    }

    private String buildLawContext(LawIdentificationResult identification) {
        StringBuilder context = new StringBuilder();

        List<LawDto> laws = identification.relevantLaws() != null
                ? identification.relevantLaws()
                : List.of();

        for (LawDto law : laws) {
            String title = lawSourceService.getLawTitle(law.number(), law.year());
            if (!title.isBlank()) {
                context.append(title).append("\n");
            }

            List<String> paragraphs = law.relevantParagraphs() != null
                    ? law.relevantParagraphs()
                    : List.of();

            if (!paragraphs.isEmpty()) {
                String paragraphTexts = lawSourceService.fetchParagraphTexts(
                        law.number(), law.year(), paragraphs
                );
                if (!paragraphTexts.isBlank()) {
                    context.append("Relevantní ustanovení:\n").append(paragraphTexts).append("\n");
                }
            }
        }

        return context.toString();
    }

    private ContractAnalysisResponse saveAndReturn(ContractAnalysisRequest request,
                                                   LawIdentificationResult identification,
                                                   AnalysisResult result) {
        ContractAnalysis analysis = new ContractAnalysis();
        analysis.setContractName(request.contractName());
        analysis.setContractType(identification.contractType());
        analysis.setOriginalContractText(request.originalContractText());
        analysis.setRevisedContractText(request.revisedContractText());
        analysis.setAddedClauses(result.addedClauses());
        analysis.setOverallRisk(result.overallRisk());
        analysis.setSummary(result.summary());

        List<AnalysisIssueResult> issueResults =
                result.issues() != null ? result.issues() : List.of();

        List<LegalIssue> issues = issueResults.stream()
                .map(issueResult -> {
                    LegalIssue issue = new LegalIssue();
                    issue.setSeverity(issueResult.severity());
                    issue.setClause(issueResult.clause());
                    issue.setProblem(issueResult.problem());
                    issue.setRecommendation(issueResult.recommendation());
                    issue.setLegalReference(issueResult.legalReference());
                    issue.setContractAnalysis(analysis);
                    return issue;
                })
                .toList();

        analysis.setIssues(issues);

        return toResponse(repository.save(analysis));
    }

    public List<ContractAnalysisResponse> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ContractAnalysisResponse findById(Long id) {
        ContractAnalysis analysis = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analýza nenalezena: " + id));
        return toResponse(analysis);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private ContractAnalysisResponse toResponse(ContractAnalysis a) {
        List<LegalIssueDto> issues = a.getIssues().stream()
                .map(i -> new LegalIssueDto(
                        i.getSeverity(),
                        i.getClause(),
                        i.getProblem(),
                        i.getRecommendation(),
                        i.getLegalReference()
                ))
                .toList();
        return new ContractAnalysisResponse(
                a.getId(),
                a.getContractName(),
                a.getContractType(),
                a.getOverallRisk(),
                a.getAddedClauses(),
                a.getSummary(),
                issues,
                a.getCreatedAt()
        );
    }

    private static final Map<String, Object> LAW_IDENTIFICATION_SCHEMA = Map.of(
            "type", "object",
            "required", List.of("contractType", "relevantLaws", "keyTopics"),
            "properties", Map.of(
                    "contractType", Map.of("type", "string"),
                    "relevantLaws", Map.of(
                            "type", "array",
                            "items", Map.of(
                                    "type", "object",
                                    "required", List.of("number", "year", "title", "relevantParagraphs"),
                                    "properties", Map.of(
                                            "number", Map.of("type", "string"),
                                            "year", Map.of("type", "string"),
                                            "title", Map.of("type", "string"),
                                            "relevantParagraphs", Map.of(
                                                    "type","array",
                                                    "items", Map.of("type", "string")
                                            )

                                    )
                            )
                    ),
                    "keyTopics", Map.of(
                            "type", "array",
                            "items", Map.of("type", "string")
                    )

            )
    );



    private static final Map<String, Object> ANALYSIS_SCHEMA = Map.of(
            "type", "object",
            "required", List.of("addedClauses", "overallRisk", "summary", "issues"),
            "properties", Map.of(
                    "addedClauses", Map.of("type", "string"),
                    "overallRisk", Map.of(
                            "type", "string",
                            "enum", List.of("HIGH", "MEDIUM", "LOW")
                    ),
                    "summary", Map.of("type", "string"),
                    "issues", Map.of(
                            "type", "array",
                            "items", Map.of(
                                    "type", "object",
                                    "required", List.of("severity", "clause", "problem", "recommendation", "legalReference"),
                                    "properties", Map.of(
                                            "severity", Map.of(
                                                    "type", "string",
                                                    "enum", List.of("HIGH", "MEDIUM", "LOW")
                                            ),
                                            "clause", Map.of("type", "string"),
                                            "problem", Map.of("type", "string"),
                                            "recommendation", Map.of("type", "string"),
                                            "legalReference", Map.of("type", "string")
                                    )
                            )
                    )
            )
    );
}


