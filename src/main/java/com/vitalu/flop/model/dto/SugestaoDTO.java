package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;

import com.vitalu.flop.model.entity.Sugestao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SugestaoDTO {

	private Long idSugestao;
	private String nomePraia;
	private String bairro;
	private String descricao;
	private Boolean analisada;
	private LocalDateTime criadaEm;
	private Long idUsuario;
	private String nomeUsuario;

	public static SugestaoDTO converterParaDTO(Sugestao sugestao) {
        return SugestaoDTO.builder()
            .idSugestao(sugestao.getIdSugestao())
            .nomePraia(sugestao.getNomePraia())
            .bairro(sugestao.getBairro())
            .descricao(sugestao.getDescricao())
            .analisada(sugestao.getAnalisada())
            .criadaEm(sugestao.getCriadaEm())
            .idUsuario(sugestao.getUsuario() != null ? sugestao.getUsuario().getIdUsuario() : null)
            .nomeUsuario(sugestao.getUsuario() != null ? sugestao.getUsuario().getNome() : null)
            .build();
    }
}
