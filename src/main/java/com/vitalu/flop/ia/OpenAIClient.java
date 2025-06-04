package com.vitalu.flop.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenAIClient {

	@Value("${openai.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();

	public String gerarResposta(String prompt) {
		String url = "https://api.openai.com/v1/chat/completions";

		OpenAIRequest request = new OpenAIRequest(prompt);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<OpenAIResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
				OpenAIResponse.class);

		return response.getBody().getFirstMessage();
	}
}
