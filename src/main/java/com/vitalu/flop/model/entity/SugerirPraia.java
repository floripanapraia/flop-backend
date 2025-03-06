package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SugerirPraia {
	
	@Id
	Long idSugestao;
	String nomePraia;
	String bairro;
	String descricao;
	Boolean analisada;
	LocalDateTime criadaEm;
	//Usuario usuario;
}
