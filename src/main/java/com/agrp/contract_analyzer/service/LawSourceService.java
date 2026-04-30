package com.agrp.contract_analyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LawSourceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
}