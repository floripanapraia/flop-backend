package com.vitalu.flop.service.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.vitalu.flop.model.dto.UsuarioDTO;
import com.vitalu.flop.model.entity.Usuario;

public class UsuarioMapper {

	public static UsuarioDTO toDTO(Usuario usuario) {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setIdUsuario(usuario.getIdUsuario());
		dto.setNome(usuario.getNome());
		dto.setUsername(usuario.getUsername());
		dto.setEmail(usuario.getEmail());
		dto.setFotoPerfil(usuario.getFotoPerfil());
		dto.setAdmin(usuario.isAdmin());
		dto.setBloqueado(usuario.isBloqueado());
		dto.setDataCriacao(usuario.getCriadoEm());

		dto.setAvaliacaoIds(usuario.getAvaliacao().stream().map(a -> a.getIdAvaliacao()).collect(Collectors.toList()));

		dto.setPostagemIds(usuario.getPostagem().stream().map(p -> p.getIdPostagem()).collect(Collectors.toList()));

		dto.setSugerirPraiaIds(
				usuario.getSugestoes().stream().map(s -> s.getIdSugestao()).collect(Collectors.toList()));

		return dto;
	}

	public static Usuario toEntity(UsuarioDTO dto) {
		Usuario usuario = new Usuario();
		usuario.setIdUsuario(dto.getIdUsuario());
		usuario.setNome(dto.getNome());
		usuario.setUsername(dto.getUsername());
		usuario.setEmail(dto.getEmail());
		usuario.setFotoPerfil(dto.getFotoPerfil());
		usuario.setAdmin(dto.isAdmin());
		usuario.setBloqueado(dto.isBloqueado());

		usuario.setAvaliacao(new ArrayList<>());
		usuario.setPostagem(new ArrayList<>());
		usuario.setSugestoes(new ArrayList<>());

		return usuario;
	}
}
