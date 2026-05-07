package com.agrp.contract_analyzer.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildIdentificationPrompt(String original, String revised) {
        return """
                Jsi zkušený právník specializovaný na české právo.
                
                Porovnej původní a upravenou verzi smlouvy.
                Urči:
                - typ smlouvy,
                - relevantní české zákony,
                - klíčová právní témata,
                - relevantní paragrafy.
                
                Uváděj pouze zákony a paragrafy, které skutečně existují v českém právním řádu.
                Pokud si nejsi jistý, daný zákon nebo paragraf neuváděj.
                
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
                Právní kontext (ověřený z právních zdrojů): %s
                
                Porovnej původní a upravenou verzi smlouvy a zaměř se na ustanovení,
                která byla v upravené verzi přidána nebo významně rozšířena.
                
                Proveď analýzu takto:
                1. Identifikuj nově přidané nebo rozšířené klauzule.
                2. U každé posuď právní riziko a soulad s českým právem.
                3. Urči závažnost rizika jako HIGH, MEDIUM nebo LOW.
                4. Vysvětli problém stručně a věcně.
                5. Navrhni konkrétní doporučení.
                6. Uveď právní odkaz pouze tehdy, pokud vyplývá z poskytnutého právního kontextu.
                
                Pokud relevantní právní odkaz není v poskytnutém kontextu, neuváděj ho
                a popiš problém obecně bez citace paragrafu.
                
                Shrnutí celé analýzy napiš česky, stručně a srozumitelně.
                
                Původní smlouva:
                %s
                
                Upravená smlouva:
                %s
                """.formatted(contractType, String.join(", ", keyTopics), lawContext, original, revised);
    }
}
