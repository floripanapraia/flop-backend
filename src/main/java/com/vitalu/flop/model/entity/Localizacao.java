package com.vitalu.flop.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Localizacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idLocalizacao;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

	@OneToOne(mappedBy = "localizacao")
	@JsonBackReference("localizacao-praia")
	private Praia praia;

}