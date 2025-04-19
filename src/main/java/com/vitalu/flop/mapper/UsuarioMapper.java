package com.vitalu.flop.mapper;

import com.vitalu.flop.model.dto.UsuarioDTO;
import com.vitalu.flop.model.entity.Usuario;

public class UsuarioMapper {

	public static UsuarioDTO toDTO(Usuario usuario) {
		if (usuario == null) {
			return null;
		}

		UsuarioDTO dto = new UsuarioDTO();
		dto.setIdUsuario(usuario.getIdUsuario());
		dto.setNome(usuario.getNome());
		dto.setEmail(usuario.getEmail());
		dto.setFotoPerfil(usuario.getFotoPerfil());
		dto.setNickname(usuario.getNickname());
		return dto;
	}

	public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setFotoPerfil(dto.getFotoPerfil());
        usuario.setNickname(dto.getNickname());
        return usuario;
    }
}
