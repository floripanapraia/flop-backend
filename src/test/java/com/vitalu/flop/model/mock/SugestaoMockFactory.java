package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;

import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.entity.Usuario;

public class SugestaoMockFactory {

    public static Sugestao criarSugestaoPadrao() {
        Sugestao sugestao = new Sugestao();
        sugestao.setIdSugestao(1L);
        sugestao.setNomePraia("Praia do Diabo");
        sugestao.setBairro("Ipanema");
        sugestao.setDescricao("Uma praia incrível com águas cristalinas e ótima para surf.");
        sugestao.setAnalisada(false);
        sugestao.setCriadaEm(LocalDateTime.now());
        sugestao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        return sugestao;
    }

    public static Sugestao criarSugestaoAnalisada() {
        Sugestao sugestao = criarSugestaoPadrao();
        sugestao.setIdSugestao(2L);
        sugestao.setNomePraia("Praia dos Ingleses");
        sugestao.setAnalisada(true);
        return sugestao;
    }

    public static Sugestao criarSugestaoComUsuarioEspecifico(Usuario usuario) {
        Sugestao sugestao = criarSugestaoPadrao();
        sugestao.setUsuario(usuario);
        return sugestao;
    }

    public static Sugestao criarSugestaoParaEdicao() {
        Sugestao sugestao = new Sugestao();
        sugestao.setIdSugestao(3L);
        sugestao.setNomePraia("Praia Editada");
        sugestao.setBairro("Bairro Editado");
        sugestao.setDescricao("Descrição editada da praia.");
        sugestao.setAnalisada(false);
        sugestao.setCriadaEm(LocalDateTime.now().minusDays(1));
        sugestao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        return sugestao;
    }

    public static Sugestao criarSugestaoSemAnalise() {
        Sugestao sugestao = criarSugestaoPadrao();
        sugestao.setIdSugestao(4L);
        sugestao.setNomePraia("Praia Pendente");
        sugestao.setAnalisada(false);
        return sugestao;
    }

    public static Sugestao criarNovaSugestao() {
        Sugestao sugestao = new Sugestao();
        sugestao.setNomePraia("Nova Praia Sugerida");
        sugestao.setBairro("Novo Bairro");
        sugestao.setDescricao("Descrição da nova praia sugerida.");
        sugestao.setAnalisada(false);
        sugestao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        return sugestao;
    }
}