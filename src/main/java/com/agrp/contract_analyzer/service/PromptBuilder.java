package com.agrp.contract_analyzer.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildIdentificationPrompt(String original, String revised) {
        return """
                Jsi zkušený právník specializovaný na české právo.
                Porovnej dvě verze smlouvy a vrať POUZE validní JSON bez markdownu.
           
                
                Formát:
                {
                  "contractType": "typ smlouvy",
                  "relevantLaws": [{"number": "89", "year": "2012", "title": "Občanský zákoník"}],
                  "keyTopics": ["téma1", "téma2"],
                  "relevantParagraphs": ["§ 2248", "§ 2249"]
                }
                
                Uveď pouze zákony a paragrafy které skutečně existují v českém právním řádu.
                
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
                Jsi zkušený právník specializovaný na české právo.
                
                Typ smlouvy: %s
                Klíčová témata: %s
                Právní kontext (ověřeno ze Zákony pro lidi): %s
                
                Analyzuj přidané klauzule krok za krokem:
                            Krok 1: Identifikuj co přibylo ve druhé verzi smlouvy.
                            Krok 2: Pro každou přidanou část urči relevantní právní oblast.
                            Krok 3: Zhodnoť jestli je klauzule v souladu se zákonem.
                            Krok 4: Urči závažnost rizika (HIGH/MEDIUM/LOW).
                            Krok 5: Navrhni konkrétní doporučení.
                
                DŮLEŽITÉ: V právním kontextu výše máš skutečné znění paragrafů ze Zákony pro lidi.
                            Cituj pouze paragrafy které jsou uvedeny v tomto kontextu.
                            Pokud paragraf v kontextu není, popiš problém obecně bez citace paragrafu.
                
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