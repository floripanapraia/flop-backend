package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;
import java.util.List;

import com.vitalu.flop.model.entity.Usuario;

public class UsuarioMockFactory {

	public static Usuario criarUsuarioPadrao() {
		Usuario usuario = new Usuario();

		usuario.setIdUsuario(1L);
		usuario.setNome("Ayrton Senna");
		usuario.setNickname("ayrton");
		usuario.setSenha("sennadobrasil");
		usuario.setEmail("ayrton@senna.com");
		usuario.setAdmin(false);
		usuario.setCriadoEm(LocalDateTime.now());
		usuario.setFotoPerfil("https://exemplo.com/foto.jpg");
		usuario.setBloqueado(false);
		usuario.setAvaliacao(List.of());
		usuario.setPostagem(List.of());
		usuario.setSugestoes(List.of());
		usuario.setForgotPassword(null);

		return usuario;
	}

	public static Usuario criarUsuarioAdmin() {
		Usuario usuario = criarUsuarioPadrao();
		usuario.setAdmin(true);
		return usuario;
	}

	public static Usuario criarUsuarioBloqueado() {
		Usuario usuario = criarUsuarioPadrao();
		usuario.setBloqueado(true);
		return usuario;
	}
}
