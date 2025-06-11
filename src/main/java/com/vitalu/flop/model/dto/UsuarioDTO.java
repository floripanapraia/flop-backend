package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UsuarioDTO {

	private Long idUsuario;
	private String nome;
	private String nickname;
	private String email;
	private boolean isAdmin;
	private LocalDateTime dataCriacao;
	private String fotoPerfil;
	private boolean bloqueado;
	private List<Long> avaliacaoIds;
	private List<Long> postagemIds;
	private List<Long> sugerirPraiaIds;

}