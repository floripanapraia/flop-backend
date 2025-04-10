package com.vitalu.flop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Localizacao;
import com.vitalu.flop.model.repository.LocalizacaoRepository;

@Service
public class LocalizacaoService {

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	public Localizacao criarLocalizacao(Localizacao novoLocal) throws FlopException {
		return localizacaoRepository.save(novoLocal);
	}

	public void excluirLocalizacao(Long localizacaoId) throws FlopException {
		Localizacao localizacao = localizacaoRepository.findById(localizacaoId)
				.orElseThrow(() -> new FlopException("Localização não encontrada.", HttpStatus.NOT_FOUND));

		try {
			localizacaoRepository.delete(localizacao);
		} catch (Exception e) {
			throw new FlopException("Erro ao excluir a localização.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
