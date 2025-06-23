package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;
import java.util.List;

import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;

public class AvaliacaoMockFactory {

    public static Avaliacao criarAvaliacaoPadrao() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(1L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(LocalDateTime.now());
        avaliacao.setCondicoes(List.of(Condicoes.SOL, Condicoes.MAR_CALMO, Condicoes.LIMPA));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoComUsuario(Usuario usuario) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(2L);
        avaliacao.setUsuario(usuario);
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(LocalDateTime.now());
        avaliacao.setCondicoes(List.of(Condicoes.NUBLADO, Condicoes.MAR_ONDAS));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoComPraia(Praia praia) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(3L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(praia);
        avaliacao.setCriadoEm(LocalDateTime.now());
        avaliacao.setCondicoes(List.of(Condicoes.SOL, Condicoes.MAR_CALMO));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoHoje() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(4L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(LocalDateTime.now());
        avaliacao.setCondicoes(List.of(Condicoes.SOL, Condicoes.LIMPA));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoOntem() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(5L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(LocalDateTime.now().minusDays(1));
        avaliacao.setCondicoes(List.of(Condicoes.CHUVA, Condicoes.MAR_ONDAS));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoComCondicoesConflitantes() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(6L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(LocalDateTime.now());
        // Condições conflitantes: SOL e CHUVA
        avaliacao.setCondicoes(List.of(Condicoes.SOL, Condicoes.CHUVA));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoComData(LocalDateTime data) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(7L);
        avaliacao.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
        avaliacao.setPraia(PraiaMockFactory.criarPraiaPadrao());
        avaliacao.setCriadoEm(data);
        avaliacao.setCondicoes(List.of(Condicoes.SOL, Condicoes.MAR_CALMO));
        return avaliacao;
    }

    public static Avaliacao criarAvaliacaoComUsuarioEPraia(Usuario usuario, Praia praia) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(8L);
        avaliacao.setUsuario(usuario);
        avaliacao.setPraia(praia);
        avaliacao.setCriadoEm(LocalDateTime.now());
        avaliacao.setCondicoes(List.of(Condicoes.NUBLADO, Condicoes.MAR_CALMO, Condicoes.LIMPA));
        return avaliacao;
    }
}