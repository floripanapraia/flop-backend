package com.vitalu.flop.mapper;

import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Praia;

public class PraiaMapper {

	public static PraiaDTO toDTO(Praia praia) {
		if (praia == null) {
			return null;
		}
		PraiaDTO dto = new PraiaDTO();
		dto.setIdPraia(praia.getIdPraia());
		dto.setNomePraia(praia.getNomePraia());
		dto.setImagem(praia.getImagem());
		dto.setLatitude(praia.getLatitude());
		dto.setLongitude(praia.getLongitude());
		dto.setPlaceId(praia.getPlaceId());

		return dto;
	}

	public static Praia toEntity(PraiaDTO dto) {
		if (dto == null) {
			return null;
		}

		Praia praia = new Praia();
		praia.setIdPraia(dto.getIdPraia());
		praia.setNomePraia(dto.getNomePraia());
		praia.setImagem(dto.getImagem());
		praia.setLatitude(dto.getLatitude());
		praia.setLongitude(dto.getLongitude());
		praia.setPlaceId(dto.getPlaceId());

		return praia;
	}

}
