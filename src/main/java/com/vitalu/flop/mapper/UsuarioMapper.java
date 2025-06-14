package com.vitalu.flop.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vitalu.flop.model.dto.UsuarioDTO;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.PostagemRepository;

@Component
public class UsuarioMapper {

	@Autowired
	private PostagemRepository postagemRepository;

	public UsuarioDTO toDTO(Usuario usuario) {
		if (usuario == null) {
			return null;
		}

		UsuarioDTO dto = new UsuarioDTO();
		dto.setIdUsuario(usuario.getIdUsuario());
		dto.setNome(usuario.getNome());
		dto.setEmail(usuario.getEmail());
		dto.setFotoPerfil(usuario.getFotoPerfil());
		dto.setNickname(usuario.getNickname());
		dto.setDataCriacao(usuario.getCriadoEm());

		// CONVERSÃO EXPLÍCITA: boolean (entidade) -> int (DTO)
		// Usa um operador ternário: (condição ? valor_se_verdadeiro : valor_se_falso)
		dto.setIsAdmin(usuario.isAdmin() ? 1 : 0);
		dto.setIsBloqueado(usuario.isBloqueado() ? 1 : 0);

		int totalBloqueadas = postagemRepository.countByUsuarioIdUsuarioAndExcluidaIsTrue(usuario.getIdUsuario());
		dto.setTotalPostagensBloqueadas(totalBloqueadas);

		if (usuario.getAvaliacao() != null) {
			dto.setAvaliacaoIds(
					usuario.getAvaliacao().stream().map(a -> a.getIdAvaliacao()).collect(Collectors.toList()));
		}
		if (usuario.getPostagem() != null) {
			dto.setPostagemIds(usuario.getPostagem().stream().map(p -> p.getIdPostagem()).collect(Collectors.toList()));
		}
		if (usuario.getSugestoes() != null) {
			dto.setSugerirPraiaIds(
					usuario.getSugestoes().stream().map(s -> s.getIdSugestao()).collect(Collectors.toList()));
		}

		return dto;
	}

	public Usuario toEntity(UsuarioDTO dto) {
		if (dto == null) {
			return null;
		}

		Usuario usuario = new Usuario();
		usuario.setIdUsuario(dto.getIdUsuario());
		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setFotoPerfil(dto.getFotoPerfil());
		usuario.setNickname(dto.getNickname());

		// CONVERSÃO EXPLÍCITA: int (DTO) -> boolean (entidade)
		// A expressão "dto.getIsAdmin() == 1" resulta em true ou false.
		usuario.setAdmin(dto.getIsAdmin() == 1);
		usuario.setBloqueado(dto.getIsBloqueado() == 1);

		return usuario;
	}
}
