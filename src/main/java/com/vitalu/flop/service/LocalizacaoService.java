package com.vitalu.flop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.repository.PraiaRepository;

@Service
public class LocalizacaoService {
	private static final double LIMITE_METROS = 1000;

	@Autowired
	private PraiaRepository praiaRepository;

	public void validarProximidadePraia(Long praiaId, double latitudeUser, double longitudeUser) throws FlopException {

		Double distancia = praiaRepository.distanciaEmMetros(praiaId, latitudeUser, longitudeUser);
		if (distancia == null) {
			throw new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND);
		}
		if (distancia > LIMITE_METROS) {
			throw new FlopException("Você só pode postar ou avaliar se estiver até 1 km da praia!!!", HttpStatus.FORBIDDEN);
		}
	}
}
