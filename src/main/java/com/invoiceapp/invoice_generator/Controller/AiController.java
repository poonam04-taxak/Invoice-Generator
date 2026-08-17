package com.invoiceapp.invoice_generator.Controller;


import com.invoiceapp.invoice_generator.DTO.DescriptionRequestDTO;
import com.invoiceapp.invoice_generator.Service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * exposes AI-assisted features for the invoice generator
 * supports generating a professional invoice line-item description
 * from a short, informal note using Google Gemini api
 */

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate-description")
    public ResponseEntity<Map<String, String>> generateDescription(@Valid @RequestBody DescriptionRequestDTO request) {
        String description = geminiService.generateItemDescription(request.getNote());
        return ResponseEntity.ok(Map.of("description", description));
    }
}
