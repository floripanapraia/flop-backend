package com.vitalu.flop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class CriarPostagemDTO extends PostagemDTO {

	private Double latitudeUser;
	private Double longitudeUser;

}
