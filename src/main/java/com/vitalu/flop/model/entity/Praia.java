package com.vitalu.flop.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

//import java.util.List;

@Entity
public class Praia {
	
	@Id
	Long idPraia;
	@Column
	String nomePraia;
	@Column
	String imagem;
	@Column
	Localizacao localizacao;
	
//	List<Avaliacao> avaliacoes;
//	List <Postagem> postagens;
}