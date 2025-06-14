package com.vitalu.flop.ia;

import java.util.List;

import lombok.Data;

@Data
public class GeminiRequest {

	private List<Content> contents;

	public GeminiRequest(String prompt) {
		this.contents = List.of(new Content(List.of(new Part(prompt))));
	}

	public static class Content {
		public List<Part> parts;

		public Content(List<Part> parts) {
			this.parts = parts;
		}
	}

	public static class Part {
		public String text;

		public Part(String text) {
			this.text = text;
		}
	}

}