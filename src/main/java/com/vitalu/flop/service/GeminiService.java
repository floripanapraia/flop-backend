package com.vitalu.flop.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalu.flop.exception.FlopException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper;

    // --- MUDANÇA 1: USANDO O MODELO "PRO" ---
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=";

    // Estruturas de classes para o JSON
    private record Part(String text) {}
    private record Content(List<Part> parts, String role) {}
    private record SafetySetting(String category, String threshold) {}
    private record GeminiRequest(List<Content> contents, List<SafetySetting> safetySettings) {}
    private record GeminiResponse(List<Candidate> candidates) {}
    private record Candidate(Content content) {}


    @PostConstruct
    public void init() {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean isMensagemConsideradaOfensiva(String message) throws FlopException {
        // --- MUDANÇA 2: PROMPT COM TOLERÂNCIA ZERO ---
        String prompt = "TAREFA: Classificação de Conteúdo. Você é um moderador de conteúdo para uma rede social sobre praias com uma política de tolerância zero. " +
                        "Sua única função é classificar o texto fornecido. INSTRUÇÕES: Se o texto contiver QUALQUER um dos seguintes elementos - " +
                        "ofensa, agressão, ameaça velada, generalização negativa sobre um grupo de pessoas, linguagem depreciativa, " +
                        "ou um tom excessivamente reclamão ou raivoso - você DEVE responder 'SIM'. Caso contrário, responda 'NAO'. " +
                        "Responda APENAS com 'SIM' ou 'NAO'. TEXTO PARA ANÁLISE: \"" + message + "\"";

        String apiUrl = GEMINI_API_URL + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<SafetySetting> safetySettings = List.of(
            new SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
            new SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
            new SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
            new SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")
        );

        List<Content> contents = List.of(new Content(List.of(new Part(prompt)), "user"));
        GeminiRequest requestBody = new GeminiRequest(contents, safetySettings);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiResponse response = restTemplate.postForObject(apiUrl, entity, GeminiResponse.class);

            if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                String respostaDaIA = response.candidates().get(0).content().parts().get(0).text();
                return "SIM".equalsIgnoreCase(respostaDaIA.trim());
            }
            
            return true;

        } catch (HttpClientErrorException e) {
            return true;
        } catch (Exception e) {
            throw new FlopException("Erro ao validar o conteúdo com a IA: " + e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}