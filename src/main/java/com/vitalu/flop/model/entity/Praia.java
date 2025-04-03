package com.vitalu.flop.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "idLocalizacao", referencedColumnName = "idLocalizacao")
//	private Localizacao localizacao;

	@OneToMany(mappedBy = "praia")
	@JsonIgnore
	private List<Avaliacao> avaliacoes;

	@OneToMany(mappedBy = "praia")
	private List<Postagem> postagens;
}