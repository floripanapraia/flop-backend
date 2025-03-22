package com.vitalu.flop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.repository.SugestaoRepository;

@Service
public class SugestaoService {

	@Autowired
	private SugestaoRepository sugestaoRepository;

	public Sugestao criarSugestao(Sugestao novaSugestao) throws FlopException {
		return sugestaoRepository.save(novaSugestao);
	}

	public void excluirSugestao(Long sugestaoId, Long usuarioId) throws FlopException {

		Sugestao sugestao = sugestaoRepository.findById(sugestaoId)
				.orElseThrow(() -> new FlopException("Sugestão não encontrada", HttpStatus.NOT_FOUND));

		if (sugestao.getUsuario().getIdUsuario().equals(usuarioId)) {
			sugestaoRepository.deleteById(sugestaoId);
		} else {
			throw new FlopException("Você não pode excluir sugestões de outras pessoas!", HttpStatus.UNAUTHORIZED);
		}

	}

	public Sugestao editarSugestao(Long sugestaoId, Sugestao editarSugestao) throws FlopException {
		Sugestao existente = sugestaoRepository.findById(sugestaoId)
				.orElseThrow(() -> new FlopException("Sugestão não encontrada!", HttpStatus.NOT_FOUND));

		existente.setNomePraia(editarSugestao.getNomePraia());
		existente.setBairro(editarSugestao.getBairro());
		existente.setDescricao(editarSugestao.getDescricao());
		existente.setAnalisada(editarSugestao.getAnalisada());

		return sugestaoRepository.save(existente);
	}

	public Sugestao procurarPorId(Long sugestaoId) throws FlopException {
		return sugestaoRepository.findById(sugestaoId)
				.orElseThrow(() -> new FlopException("Esta sugestão não foi encontrada!", HttpStatus.NOT_FOUND));
	}

	public Page<Sugestao> pesquisarSugestaoFiltros(Long sugestaoId) throws FlopException {
		// TODO
		return null;
	}
}
