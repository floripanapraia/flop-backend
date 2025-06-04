package com.vitalu.flop.ia;

import org.springframework.stereotype.Service;

@Service
public class IAService {

	private final OpenAIClient client;

	public IAService(OpenAIClient client) {
		this.client = client;
	}

	public String gerarClausulaSQL(String preferencias) {
		String prompt = """
				Você é um assistente que gera cláusulas SQL baseadas em condições para buscar praias em um banco de dados.
				Use SOMENTE os seguintes valores do campo `condicoes`:

				NUBLADO, VENTO, CHUVA, SOL, LOTADA, AGUA_VIVA, LIXO, LIMPA, MAR_ONDAS, MAR_CALMO, MUSICA, ALIMENTACAO, SALVA_VIDAS,
				AGUA_GELADA, ESTACIONAMENTO

				O campo `condicoes` é armazenado como uma lista de strings. Gere a cláusula WHERE com base no texto:

				Texto: "%s"
				"""
				.formatted(preferencias);

		return client.gerarResposta(prompt);
	}
}
