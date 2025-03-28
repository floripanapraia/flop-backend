package com.vitalu.flop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Localizacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idLocalizacao;

	private String idGooglePlace;

	private Double longitude;

	private Double latitude;

	private String cidade;

	private String estado;

//	@OneToOne(mappedBy = "localizacao")
//	private Praia praia;

}
