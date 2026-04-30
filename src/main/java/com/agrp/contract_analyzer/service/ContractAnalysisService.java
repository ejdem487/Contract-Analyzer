package com.agrp.contract_analyzer.service;

import com.agrp.contract_analyzer.dto.*;
import com.agrp.contract_analyzer.model.ContractAnalysis;
import com.agrp.contract_analyzer.model.LegalIssue;
import com.agrp.contract_analyzer.repository.ContractAnalysisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContractAnalysisService {

    private final GeminiService geminiService;
    private final LawSourceService lawSourceService;
    private final ContractAnalysisRepository repository;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContractAnalysisService(GeminiService geminiService,
                                   LawSourceService lawSourceService,
                                   ContractAnalysisRepository repository,
                                   PromptBuilder promptBuilder) {
        this.geminiService = geminiService;
        this.lawSourceService = lawSourceService;
        this.repository = repository;
        this.promptBuilder = promptBuilder;
    }

    public ContractAnalysisResponse analyze(ContractAnalysisRequest request) {

        // 1. První Gemini volání — identifikace zákonů
        LawIdentificationResult identification = identifyLaws(
                request.originalContractText(),
                request.revisedContractText()
        );

        // 2. Ověření zákonů přes zakonyprolidi.cz
        String lawContext = buildLawContext(identification);

        // 3. Druhé Gemini volání — analýza rizik
        String analysisJson = geminiService.generate(
                promptBuilder.buildAnalysisPrompt(
                        request.originalContractText(),
                        request.revisedContractText(),
                        identification.contractType(),
                        identification.keyTopics(),
                        lawContext
                )
        );

        // 4. Uložení a vrácení výsledku
        return saveAndReturn(request, identification, analysisJson);
    }

    private LawIdentificationResult identifyLaws(String original, String revised) {
        String prompt = promptBuilder.buildIdentificationPrompt(original, revised);
        String response = geminiService.generate(prompt);

        try {
            String clean = response.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(clean, LawIdentificationResult.class);
        } catch (Exception e) {
            return new LawIdentificationResult("neznámá", List.of(), List.of());
        }
    }

    private String buildLawContext(LawIdentificationResult identification) {
        String context = "";
        for (LawDto law : identification.relevantLaws()) {
            String title = lawSourceService.getLawTitle(law.number(), law.year());
            if (!title.isBlank()) {
                context += title + "\n";
            }
        }
        return context;
    }

    private ContractAnalysisResponse saveAndReturn(ContractAnalysisRequest request,
                                                   LawIdentificationResult identification,
                                                   String analysisJson) {
        try {
            String clean = analysisJson.replaceAll("```json", "").replaceAll("```", "").trim();
            JsonNode root = objectMapper.readTree(clean);

            ContractAnalysis analysis = new ContractAnalysis();
            analysis.setContractName(request.contractName());
            analysis.setContractType(identification.contractType());
            analysis.setOriginalContractText(request.originalContractText());
            analysis.setRevisedContractText(request.revisedContractText());
            analysis.setAddedClauses(root.path("addedClauses").asText(""));
            analysis.setOverallRisk(root.path("overallRisk").asText("UNKNOWN"));
            analysis.setSummary((root.path("summary").asText("")));

            List<LegalIssue> issues = new ArrayList<>();
            for (JsonNode issueNode : root.path("issues")) {
                LegalIssue issue = new LegalIssue();
                issue.setSeverity(issueNode.path("severity").asText(""));
                issue.setClause(issueNode.path("clause").asText(""));
                issue.setProblem(issueNode.path("problem").asText(""));
                issue.setRecommendation(issueNode.path("recommendation").asText(""));
                issue.setLegalReference(issueNode.path("legalReference").asText(""));
                issue.setContractAnalysis(analysis);
                issues.add(issue);
            }
            analysis.setIssues(issues);

            ContractAnalysis saved = repository.save(analysis);

            List<LegalIssueDto> issueDtos = saved.getIssues().stream()
                    .map(i -> new LegalIssueDto(
                            i.getSeverity(),
                            i.getClause(),
                            i.getProblem(),
                            i.getRecommendation(),
                            i.getLegalReference()
                    ))
                    .toList();

            return new ContractAnalysisResponse(
                    saved.getId(),
                    saved.getContractName(),
                    saved.getContractType(),
                    saved.getOverallRisk(),
                    saved.getAddedClauses(),
                    saved.getSummary(),
                    issueDtos,
                    saved.getCreatedAt()
            );

        } catch (Exception e) {
            throw new RuntimeException("Chyba při parsování AI odpovědi: " + e.getMessage());
        }
    }

    public List<ContractAnalysisResponse> findAll() {
        return repository.findAll().stream()
                .map(a -> new ContractAnalysisResponse(
                        a.getId(),
                        a.getContractName(),
                        a.getContractType(),
                        a.getOverallRisk(),
                        a.getAddedClauses(),
                        a.getSummary(),
                        a.getIssues().stream()
                                .map(i -> new LegalIssueDto(
                                        i.getSeverity(),
                                        i.getClause(),
                                        i.getProblem(),
                                        i.getRecommendation(),
                                        i.getLegalReference()
                                ))
                                .toList(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    public ContractAnalysisResponse findById(Long id) {
        ContractAnalysis a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analýza nenalezena: " + id));
        return new ContractAnalysisResponse(
                a.getId(),
                a.getContractName(),
                a.getContractType(),
                a.getOverallRisk(),
                a.getAddedClauses(),
                a.getSummary(),
                a.getIssues().stream()
                        .map(i -> new LegalIssueDto(
                                i.getSeverity(),
                                i.getClause(),
                                i.getProblem(),
                                i.getRecommendation(),
                                i.getLegalReference()
                        ))
                        .toList(),
                a.getCreatedAt()
        );
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}