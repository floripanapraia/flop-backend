package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vitalu.flop.model.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;

@Data
public class UsuarioDTO {
	private Long idUsuario;
	private String nome;
	private String username;
	private String senha;
	private String email;
	private boolean isAdmin;
	private LocalDateTime dataCriacao;
	private String fotoPerfil;
	private boolean bloqueado;
	// private Localizacao localizacao;
	private List<Long> avaliacaoIds;
	private List<Long> postagemIds;
	private List<Long> sugerirPraiaIds;

	public Usuario toEntity(PasswordEncoder encoder) {
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(this.idUsuario);
		usuario.setNome(this.nome);
		usuario.setUsername(this.username);
		if (this.senha != null && !this.senha.isEmpty()) {
			usuario.setSenha(encoder.encode(this.senha));
		}
		usuario.setEmail(this.email);
		usuario.setAdmin(this.isAdmin);
		usuario.setFotoPerfil(this.fotoPerfil);
		usuario.setBloqueado(this.bloqueado);

		usuario.setAvaliacao(new ArrayList<>());
		usuario.setPostagem(new ArrayList<>());
		usuario.setSugestoes(new ArrayList<>());

		return usuario;
	}
}