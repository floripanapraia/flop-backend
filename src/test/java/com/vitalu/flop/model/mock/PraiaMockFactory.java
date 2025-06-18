package com.vitalu.flop.model.mock;

import java.util.ArrayList;
import java.util.List;

import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;

public class PraiaMockFactory {

    public static Praia criarPraiaPadrao() {
        Praia praia = new Praia();
        praia.setIdPraia(1L);
        praia.setNomePraia("Praia de Copacabana");
        praia.setLatitude(-22.9711);
        praia.setLongitude(-43.1822);
        praia.setPlaceId("ChIJAcWEdiN_mwARgKRnTE6jAP8");
        praia.setImagem("https://exemplo.com/copacabana.jpg");
        praia.setAvaliacoes(new ArrayList<>());
        praia.setPostagens(new ArrayList<>());
        return praia;
    }

    public static Praia criarPraiaIpanema() {
        Praia praia = new Praia();
        praia.setIdPraia(2L);
        praia.setNomePraia("Praia de Ipanema");
        praia.setLatitude(-22.9838);
        praia.setLongitude(-43.2096);
        praia.setPlaceId("ChIJm7Ex8MN_mwARgKRnTESample");
        praia.setImagem("https://exemplo.com/ipanema.jpg");
        praia.setAvaliacoes(new ArrayList<>());
        praia.setPostagens(new ArrayList<>());
        return praia;
    }

    public static Praia criarPraiaComAvaliacoes() {
        Praia praia = criarPraiaPadrao();
        List<Avaliacao> avaliacoes = new ArrayList<>();
        // Adicionar avaliações mock se necessário
        praia.setAvaliacoes(avaliacoes);
        return praia;
    }

    public static Praia criarPraiaComPostagens() {
        Praia praia = criarPraiaPadrao();
        List<Postagem> postagens = new ArrayList<>();
        // Adicionar postagens mock se necessário
        praia.setPostagens(postagens);
        return praia;
    }

    public static Praia criarPraiaSemCoordenadas() {
        Praia praia = new Praia();
        praia.setIdPraia(3L);
        praia.setNomePraia("Praia Sem Coordenadas");
        praia.setLatitude(null);
        praia.setLongitude(null);
        praia.setPlaceId("ChIJSamplePlaceId");
        praia.setImagem("https://exemplo.com/sem-coordenadas.jpg");
        praia.setAvaliacoes(new ArrayList<>());
        praia.setPostagens(new ArrayList<>());
        return praia;
    }
}