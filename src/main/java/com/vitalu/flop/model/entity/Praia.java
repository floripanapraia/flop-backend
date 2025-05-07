package com.vitalu.flop.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
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

	@Column(columnDefinition = "LONGTEXT")
	private String imagem;

	private Double latitude;

	private Double longitude;

	private String placeId;

	@OneToMany(mappedBy = "praia")
	@JsonManagedReference("avaliacao-praia")
	private List<Avaliacao> avaliacoes;

	@OneToMany(mappedBy = "praia")
	@JsonManagedReference("postagem-praia")
	private List<Postagem> postagens;
}