package com.agrp.contract_analyzer.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeminiService {
    private final Client geminiClient;

    public GeminiService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String generate(String prompt){
        GenerateContentResponse response = geminiClient.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );

        return response.text();
    }

    public String generateStructured(String prompt, Map<String, Object> jsonSchema) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .thinkingConfig(ThinkingConfig.builder().thinkingBudget(1024).build())
                .responseMimeType("application/json")
                .responseJsonSchema(jsonSchema)
                .build();

        GenerateContentResponse response = geminiClient.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                config
        );

        return response.text();
    }
}
