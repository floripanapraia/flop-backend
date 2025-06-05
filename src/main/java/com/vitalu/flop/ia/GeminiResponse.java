package com.vitalu.flop.ia;

import java.util.List;

import lombok.Data;

@Data
public class GeminiResponse {
	private List<Candidate> candidates;

	public String getFirstText() {
		return candidates.get(0).content.parts.get(0).text;
	}

	public static class Candidate {
		public Content content;
	}

	public static class Content {
		public List<Part> parts;
	}

	public static class Part {
		public String text;
	}

}
