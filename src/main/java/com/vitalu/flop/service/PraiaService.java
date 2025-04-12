package com.vitalu.flop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;

import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Localizacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.LocalizacaoRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class PraiaService {

	@Autowired
	private PraiaRepository praiaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	public Praia cadastrarPraia(PraiaDTO dto) throws FlopException {
		if (dto.getIdLocalizacao() == null) {
			throw new FlopException("ID da localização é obrigatório.", HttpStatus.BAD_REQUEST);
		}

		Optional<Localizacao> local = localizacaoRepository.findById(dto.getIdLocalizacao());

		Praia novaPraia = new Praia();
		novaPraia.setNomePraia(dto.getNomePraia());
		novaPraia.setLocalizacao(
				local.orElseThrow(() -> new FlopException("Localização não encontrada.", HttpStatus.BAD_REQUEST)));

		return praiaRepository.save(novaPraia);
	}

	public List<Praia> pesquisarPraiaTodas() throws FlopException {
		return praiaRepository.findAll();
	}

	public Praia pesquisarPraiasId(Long praiaId) throws FlopException {
		return praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Esta praia não foi localizada!", HttpStatus.NOT_FOUND));
	}

	public Page<Praia> pesquisarPraiaFiltros(Long praiaId) throws FlopException {
		// TODO
		return null;
	}

	public void excluirPraia(Long praiaId, Long usuarioId) throws FlopException {

		praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new FlopException("Usuário não localizado.", HttpStatus.NOT_FOUND));

		if (usuario.isAdmin()) {
			praiaRepository.deleteById(praiaId);
		} else {
			throw new FlopException("Apenas administradores podem excluir praias!", HttpStatus.UNAUTHORIZED);
		}
	}

	public Praia editarPraia(Long praiaId, Praia editarPraia) throws FlopException {
		Praia existente = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));

		// TODO PARTE DE LOCALIZAÇÃO
		existente.setNomePraia(editarPraia.getNomePraia());
		existente.setImagem(editarPraia.getImagem());
		existente.setAvaliacoes(editarPraia.getAvaliacoes());
		existente.setPostagens(editarPraia.getPostagens());

		return praiaRepository.save(existente);
	}

	// TODO MÉTODOS DE IA

}
