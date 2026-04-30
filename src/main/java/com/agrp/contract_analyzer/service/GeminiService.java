package com.agrp.contract_analyzer.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

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
}
