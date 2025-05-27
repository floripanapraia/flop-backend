package com.vitalu.flop.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostagemDTO {

    private Long idPostagem;
    private Long usuarioId;
    private String nickname;
    private Long praiaId;
    private String nomePraia;   
    private LocalDateTime criadoEm;
    private String imagem;
    private String mensagem;
    private Boolean excluida;

}
