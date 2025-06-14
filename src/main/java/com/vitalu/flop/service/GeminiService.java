//package com.vitalu.flop.service;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import com.google.cloud.vertexai.VertexAI;
//import com.google.cloud.vertexai.api.GenerateContentResponse;
//import com.google.cloud.vertexai.generativeai.preview.GenerativeModel;
//import com.google.cloud.vertexai.generativeai.preview.ResponseHandler;
//import com.vitalu.flop.exception.FlopException;
//
//import jakarta.annotation.PostConstruct;
//
//@Service
//public class GeminiService {
//
//	@Value("${gemini.project-id}")
//	private String projectId;
//
//	@Value("${gemini.api.key}")
//	private String apiKey;
//
//	private VertexAI vertexAI;
//	private GenerativeModel model;
//
//	@PostConstruct
//	public void init() throws IOException {
//		// Inicializa o cliente do Vertex AI.
//		this.vertexAI = new VertexAI(projectId, "us-central1");
//		this.model = new GenerativeModel("gemini-1.5-flash-001", vertexAI);
//	}
//
//	public boolean isContentInappropriate(String message) throws FlopException {
//		try {
//			// Um prompt direto e claro para a tarefa de moderação
//			String prompt = "Analise o texto a seguir e determine se ele contém conteúdo sexualmente explícito, discurso de ódio ou palavras impróprias para uma plataforma social familiar. Responda apenas com 'INAPROPRIADO' se o conteúdo for inadequado, ou 'APROPRIADO' caso contrário. Texto: \""
//					+ message + "\"";
//
//			GenerateContentResponse response = this.model.generateContent(prompt);
//			String textResponse = ResponseHandler.getText(response).trim().toUpperCase();
//
//			// Retorna true se a resposta for "INAPROPRIADO"
//			return "INAPROPRIADO".equals(textResponse);
//
//		} catch (Exception e) {
//			// Lança uma exceção se a API falhar, para que a postagem não seja criada por
//			// engano.
//			throw new FlopException("Erro ao validar o conteúdo com a IA. Tente novamente mais tarde.",
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//}