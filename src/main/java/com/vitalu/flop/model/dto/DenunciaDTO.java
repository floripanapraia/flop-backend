package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;

import com.vitalu.flop.model.enums.MotivosDenuncia;
import com.vitalu.flop.model.enums.StatusDenuncia;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DenunciaDTO {

	private String id;
	private String nomeDenunciante;
	private String postagemId;
	private String textoPostagem;
	private String imagemPostagem;
	private String usuarioId;
	private String nomeUsuario;
	private MotivosDenuncia motivo;
	private StatusDenuncia status;
	private LocalDateTime criadoEm;
	
}