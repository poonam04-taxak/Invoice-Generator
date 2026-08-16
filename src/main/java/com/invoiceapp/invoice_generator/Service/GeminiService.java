package com.invoiceapp.invoice_generator.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateItemDescription(String rawNote) {
        String prompt = "You are helping write a professional invoice line item description. "
                + "Given this short note from a freelancer/business: \"" + rawNote + "\", "
                + "rewrite it as a single, professional, client-facing invoice line item description. "
                + "Keep it under 15 words. Return ONLY the description text, no quotes, no explanation, no markdown.";

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String response = restTemplate.postForObject(apiUrl, entity, String.class);


            JsonNode root = objectMapper.readTree(response);

            String generatedText = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            return generatedText.trim();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate description: " + e.getMessage());
        }
    }
}