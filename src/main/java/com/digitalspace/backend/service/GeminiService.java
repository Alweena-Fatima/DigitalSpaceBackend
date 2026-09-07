package com.digitalspace.backend.service;
import org.springframework.beans.factory.annotation.Value;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;
@Service
public class GeminiService {
    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }
    //create prompt
    public String getWordMeaning(String word){
        String prompt = """
        Give only the simple meaning of the word "%s".
        Do not give examples, explanations, or extra information.
        Keep it to one or two short sentences.
        """.formatted(word);
        GenerateContentResponse response = client.models.generateContent(
                "gemini-3.6-flash",
                prompt,
                null
        );
        return response.text();
    }



}
