package com.vitalu.flop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Localizacao {

	@Id
	Long idLocalizacao;
	String idGooglePlace;
	Double longitude;
	Double latitude;
	String cidade;
	String estado;
	
}
