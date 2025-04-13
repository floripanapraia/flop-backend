package com.vitalu.flop.model.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PraiaDTO {
	private Long idPraia;
	private String nomePraia;
	private String imagem;
	private Long idLocalizacao;
	private List<String> mensagensPostagens;
	private List<String> imagensPostagens;
	private Map<String, Integer> condicoesAvaliacoes;

}
