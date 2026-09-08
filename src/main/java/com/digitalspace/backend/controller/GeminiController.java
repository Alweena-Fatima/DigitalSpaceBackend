package com.digitalspace.backend.controller;

import com.digitalspace.backend.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/meaning")
    public String getMeaning(@RequestParam String word) {
        return geminiService.getWordMeaning(word);
    }
}