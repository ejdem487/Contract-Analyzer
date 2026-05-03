package com.agrp.contract_analyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LawSourceService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LawSourceService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getLawTitle(String number, String year) {
        String url = "https://www.zakonyprolidi.cz/api/v1/data.json/DocData"
                + "?apikey=test"
                + "&Collection=cs"
                + "&Document=" + year + "-" + number;

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isBlank()) return "";

            String title = objectMapper.readTree(response)
                    .path("Result")
                    .path("Head")
                    .path("Short")
                    .asText("");

            return title.isBlank() ? "" : "Zákon: " + title + " (" + number + "/" + year + " Sb.)";

        } catch (Exception e) {
            return "";
        }
    }

    public String fetchParagraphTexts(String number, String year, List<String> paragraphs) {
        String url = "https://www.zakonyprolidi.cz/api/v1/data.json/DocData"
                + "?apikey=test"
                + "&Collection=cs"
                + "&Document=" + year + "-" + number;

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isBlank()) return "";

            JsonNode fragments = objectMapper.readTree(response)
                    .path("Result")
                    .path("Fragments");

            String result = "";
            for (JsonNode fragment : fragments) {
                String quote = fragment.path("Quote").asText("");
                String content = fragment.path("Content").asText("");

                for (String paragraph : paragraphs) {
                    if (quote.contains(paragraph) && !content.isBlank()) {
                        content = content.replaceAll("<[^>]+>", "");
                        result += quote + ": " + content + "\n";
                    }
                }
            }
            // jen test pro kontrolu zda fetchParagraphTexts opravdu hleda v zakonyprolidi.cz api
            System.out.println("Fetching paragraphs: " + paragraphs + " for law: " + year + "-" + number);
            System.out.println("Found paragraph text: " + result);
            return result;

        } catch (Exception e) {
            return "";
        }
    }
}