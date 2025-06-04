package com.vitalu.flop.ia;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OpenAIRequest {
	private String model = "gpt-3.5-turbo";
	private List<Map<String, String>> messages;

	public OpenAIRequest(String prompt) {
		this.messages = List.of(Map.of("role", "user", "content", prompt));
	}

}
