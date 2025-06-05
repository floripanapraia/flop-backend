package com.vitalu.flop.service;

import org.springframework.stereotype.Service;

import com.vitalu.flop.ia.GeminiClient;

@Service
public class IAService {

	private final GeminiClient geminiClient;

	public IAService(GeminiClient geminiClient) {
		this.geminiClient = geminiClient;
	}

	public String gerarQueryComBaseEmPreferencias(String preferencias) {
		String prompt = """
				Você é um assistente que gera consultas SQL para buscar a praia com mais avaliações que contenham determinadas condições.

				### Informações importantes:

				- As condições válidas são:
				  'NUBLADO', 'VENTO', 'CHUVA', 'SOL', 'LOTADA', 'AGUA_VIVA', 'LIXO', 'LIMPA', 'MAR_ONDAS', 'MAR_CALMO',
				  'MUSICA', 'ALIMENTACAO', 'SALVA_VIDAS', 'AGUA_GELADA', 'ESTACIONAMENTO'.

				- As tabelas são:
				  - `praia(id_praia, nome_praia, ...)`
				  - `avaliacao(id_avaliacao, praia_id, ...)`
				  - `avaliacao_condicoes(avaliacao_id_avaliacao, condicoes)`

				- Cada avaliação pode conter várias condições.

				### Tarefa:
				Gere uma consulta SQL que:
				1. Filtra apenas avaliações que possuem TODAS as condições fornecidas.
				2. Agrupa o resultado por praia.
				3. Conta o número de avaliações por praia que satisfazem as condições.
				4. Ordena pela quantidade (decrescente).
				5. Retorna o nome da praia e a quantidade.

				Entrada do usuário: "%s"
				"""
				.formatted(preferencias);

		return geminiClient.gerarResposta(prompt);
	}
}
