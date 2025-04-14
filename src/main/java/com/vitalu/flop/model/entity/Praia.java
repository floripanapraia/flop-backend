package com.vitalu.flop.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Praia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPraia;

	@NotBlank(message = "O nome da praia é obrigatório")
	private String nomePraia;

	private String imagem;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idLocalizacao", referencedColumnName = "idLocalizacao")
	@JsonManagedReference("localizacao-praia")
	private Localizacao localizacao;

	@OneToMany(mappedBy = "praia")
	@JsonManagedReference("avaliacao-praia")
	private List<Avaliacao> avaliacoes;

	@OneToMany(mappedBy = "praia")
	@JsonManagedReference("postagem-praia")
	private List<Postagem> postagens;
}