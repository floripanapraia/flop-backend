package com.vitalu.flop.model.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

//import java.util.List;

@Entity
@Data
public class Praia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPraia;
	private String nomePraia;
	private String imagem;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idLocalizacao", referencedColumnName = "idLocalizacao")
	private Localizacao localizacao;
	
	@OneToMany(mappedBy = "praia")
	private List<Avaliacao> avaliacoes;
	
	@OneToMany(mappedBy = "praia")
	private List<Postagem> postagens;
}