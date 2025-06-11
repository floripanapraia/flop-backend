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

        // Conversão explícita de boolean para int
        dto.setIsAdmin(usuario.isAdmin() ? 1 : 0);
        dto.setIsBloqueado(usuario.isBloqueado() ? 1 : 0);

        dto.setDataCriacao(usuario.getCriadoEm());

        // Mapear as IDs das listas
        if (usuario.getAvaliacao() != null) {
            dto.setAvaliacaoIds(usuario.getAvaliacao()
                .stream().map(a -> a.getIdAvaliacao()).toList());
        }

        if (usuario.getPostagem() != null) {
            dto.setPostagemIds(usuario.getPostagem()
                .stream().map(p -> p.getIdPostagem()).toList());
        }

        if (usuario.getSugestoes() != null) {
            dto.setSugerirPraiaIds(usuario.getSugestoes()
                .stream().map(s -> s.getIdSugestao()).toList());
        }

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

        usuario.setAdmin(dto.getIsAdmin() == 1);
        usuario.setBloqueado(dto.getIsBloqueado() == 1);

        return usuario;
    }
}
