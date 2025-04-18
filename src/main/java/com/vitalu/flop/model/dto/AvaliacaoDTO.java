package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.vitalu.flop.model.enums.Condicoes;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvaliacaoDTO {

	private Long idAvaliacao;
	private String username;
	private LocalDateTime criadoEm;
	private List<Condicoes> condicoes;
	private Long idUsuario;
	private Long idPraia;

}
