package com.agrp.contract_analyzer.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildIdentificationPrompt(String original, String revised) {
        return """
                Jsi právní analytik systému CODEXIS.
                Porovnej dvě verze smlouvy a vrať POUZE validní JSON bez markdownu.
                
                Formát:
                {
                  "contractType": "typ smlouvy",
                  "relevantLaws": [{"number": "89", "year": "2012", "title": "Občanský zákoník"}],
                  "keyTopics": ["téma1", "téma2"],
                  "relevantParagraphs": ["§ 2248", "§ 2249"]
                }
                
                Původní smlouva:
                %s
                
                Upravená smlouva:
                %s
                """.formatted(original, revised);
    }

    public String buildAnalysisPrompt(String original, String revised,
                                      String contractType, List<String> keyTopics,
                                      String lawContext) {
        return """
                Jsi právní analytik systému CODEXIS.
                Porovnej dvě verze smlouvy, najdi co přibylo a analyzuj právní rizika.
                
                Typ smlouvy: %s
                Klíčová témata: %s
                Právní kontext (ověřeno ze Zákony pro lidi): %s
                
                Původní smlouva:
                %s
                
                Upravená smlouva:
                %s
                
                Vrať POUZE validní JSON bez markdownu:
                {
                  "addedClauses": "text který přibyl",
                  "overallRisk": "HIGH/MEDIUM/LOW",
                  "summary": "shrnutí analýzy v češtině",
                  "issues": [
                    {
                      "severity": "HIGH/MEDIUM/LOW",
                      "clause": "název klauzule",
                      "problem": "popis problému v češtině",
                      "recommendation": "doporučení v češtině",
                      "legalReference": "§ a zákon"
                    }
                  ]
                }
                """.formatted(contractType, String.join(", ", keyTopics), lawContext, original, revised);
    }
}