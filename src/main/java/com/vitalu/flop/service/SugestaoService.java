package com.vitalu.flop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.SugestaoDTO;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.repository.SugestaoRepository;
import com.vitalu.flop.model.seletor.SugestaoSeletor;

@Service
public class SugestaoService {

	@Autowired
	private SugestaoRepository sugestaoRepository;

	public Sugestao criarSugestao(Sugestao novaSugestao) throws FlopException {
		return sugestaoRepository.save(novaSugestao);
	}

	public void excluirSugestao(Long sugestaoId, Long usuarioId) throws FlopException {

		Sugestao sugestao = sugestaoRepository.findById(sugestaoId)
				.orElseThrow(() -> new FlopException("Sugestão não encontrada.", HttpStatus.NOT_FOUND));

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

	public List<SugestaoDTO> pesquisarSugestaoTodas() throws FlopException {
		List<Sugestao> sugestoes = sugestaoRepository.findAll();
		List<SugestaoDTO> sugestoesDTO = new ArrayList<SugestaoDTO>();

		for (Sugestao sugestao : sugestoes) {
			SugestaoDTO dto = SugestaoDTO.converterParaDTO(sugestao);
			sugestoesDTO.add(dto);
		}

		return sugestoesDTO;
	}

	public SugestaoDTO procurarPorId(Long sugestaoId) throws FlopException {
		Sugestao sugestao = sugestaoRepository.findById(sugestaoId)
				.orElseThrow(() -> new FlopException("Esta sugestão não foi encontrada!", HttpStatus.NOT_FOUND));

		SugestaoDTO dto = SugestaoDTO.converterParaDTO(sugestao);
		return dto;
	}

	public Page<SugestaoDTO> pesquisarSugestaoFiltros(SugestaoSeletor seletor) throws FlopException {
		Pageable pageable = Pageable.unpaged();

		if (seletor.temPaginacao()) {
			pageable = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("criadaEm").ascending());
		}

		Page<Sugestao> sugestoes = sugestaoRepository.findAll(seletor, pageable);

		return sugestoes.map(SugestaoDTO::converterParaDTO);

	}

	public Sugestao alternarStatusAnalise(Long idSugestao) throws FlopException {
		Sugestao sugestao = sugestaoRepository.findById(idSugestao)
				.orElseThrow(() -> new FlopException("Esta sugestão não foi encontrada!", HttpStatus.NOT_FOUND));

		sugestao.setAnalisada(!sugestao.getAnalisada());
		return sugestaoRepository.save(sugestao);
	}
}
