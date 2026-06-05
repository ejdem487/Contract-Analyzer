package com.agrp.contract_analyzer.service;

import com.agrp.contract_analyzer.dto.ChatRequest;
import com.agrp.contract_analyzer.dto.ChatResponse;
import com.agrp.contract_analyzer.model.ContractAnalysis;
import com.agrp.contract_analyzer.model.LegalIssue;
import com.agrp.contract_analyzer.repository.ContractAnalysisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ChatAskService {

    private final ContractAnalysisRepository contractAnalysisRepository;
    private final GeminiService geminiService;
    private final PromptBuilder promptBuilder;

    public ChatAskService(ContractAnalysisRepository contractAnalysisRepository,  GeminiService geminiService, PromptBuilder promptBuilder) {
        this.contractAnalysisRepository = contractAnalysisRepository;
        this.geminiService = geminiService;
        this.promptBuilder = promptBuilder;
    }

        // metoda ktera bude volana v controlleru
    public ChatResponse ask (Long id, ChatRequest chatRequest){

        String content = promptBuilder.buildAskPrompt(buildContractContext(id),chatRequest.ask()); // creating prompt based on contract content

        String answer = normalizeAnswer(geminiService.generate(content)); // passing created prompt and calling ai

        return new ChatResponse(answer);
    }

    private String buildContractContext(Long id){
        StringBuilder context = new StringBuilder();

                StringBuilder issueContext = new StringBuilder();




                ContractAnalysis analysis = contractAnalysisRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Analýza nenalezena: " + id));




                if (analysis.getIssues() != null && !analysis.getIssues().isEmpty()) {
                    int issueContextCounter = 1;

                    for (LegalIssue issue : analysis.getIssues()) {
                        issueContext.append("Problém ").append(issueContextCounter).append(":\n");
                        issueContext.append("Popis problému:\n").append(safe(issue.getProblem())).append("\n\n");
                        issueContext.append("Klauzule:\n").append(safe(issue.getClause())).append("\n\n");
                        issueContext.append("Právní zdroj:\n").append(safe(issue.getLegalReference())).append("\n\n");
                        issueContext.append("Závažnost:\n").append(safe(issue.getSeverity())).append("\n\n");
                        issueContext.append("Doporučení:\n").append(safe(issue.getRecommendation())).append("\n\n");

                        issueContextCounter++;

                    }

                } else {
                    issueContext.append("Žádné identifikované problémy nejsou uložené.\n\n");
                }


                    context.append("Název smlouvy:\n").append(safe(analysis.getContractName())).append("\n\n");
                    context.append("Typ smlouvy:\n").append(safe(analysis.getContractType())).append("\n\n");
                    context.append("Celková úroveň právního rizika podle uložené analýzy:\n").append(safe(analysis.getOverallRisk())).append("\n\n");
                    context.append("Shrnutí:\n").append(safe(analysis.getSummary())).append("\n\n");
                    context.append("Identifikované problémy smlouvy:\n").append(issueContext).append("\n\n");
                    context.append("Přidaný text z druhé smlouvy oproti původní:\n").append(safe(analysis.getAddedClauses())).append("\n\n");
                    context.append("Původní smlouva:\n").append(safe(analysis.getOriginalContractText())).append("\n\n");
                    context.append("Upravená smlouva:\n").append(safe(analysis.getRevisedContractText())).append("\n\n");



                return context.toString();

    }

    private String safe(String value){
        return value == null || value.isBlank() ?  "Neuvedeno" : value;
    }

    private String normalizeAnswer(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

}
