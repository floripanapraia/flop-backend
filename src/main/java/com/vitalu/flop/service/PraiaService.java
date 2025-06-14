package com.vitalu.flop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.mapper.PraiaMapper;
import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.PraiaSeletor;

@Service
public class PraiaService {

	@Autowired
	private PraiaRepository praiaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private PostagemRepository postagemRepository;

	public Praia cadastrarPraia(PraiaDTO dto) throws FlopException {

		Praia novaPraia = new Praia();
		novaPraia.setNomePraia(dto.getNomePraia());
		novaPraia.setLatitude(dto.getLatitude());
		novaPraia.setLongitude(dto.getLongitude());
		novaPraia.setPlaceId(dto.getPlaceId());
		novaPraia.setImagem(dto.getImagem());

		return praiaRepository.save(novaPraia);
	}

	public void excluirPraia(Long praiaId) throws FlopException {
		praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não encontrada", HttpStatus.NOT_FOUND));

		praiaRepository.deleteById(praiaId);
	}

	public List<PraiaDTO> pesquisarPraiaTodas() throws FlopException {
		List<Praia> praias = praiaRepository.findAll();
		List<PraiaDTO> praiasDTO = new ArrayList<>();

		for (Praia praia : praias) {
			praiasDTO.add(buildDetalhesPraiaDTO(praia));
		}

		return praiasDTO;
	}

	public Page<PraiaDTO> pesquisarPraiaFiltros(PraiaSeletor seletor) throws FlopException {
		Pageable pageable = seletor.temPaginacao()
				? PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("nomePraia").ascending())
				: Pageable.unpaged();

		Page<Praia> pagePraias = praiaRepository.findAll(seletor, pageable);
		
		if (seletor.getCondicoes() != null && !seletor.getCondicoes().isEmpty()) {
			  LocalDateTime hojeInicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
			  LocalDateTime hojeFim = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

			  List<Praia> praiasCondicoes = praiaRepository.findPraiasComCondicoesHoje(
			      hojeInicio,
			      hojeFim,
			      seletor.getCondicoes(),
			      seletor.getCondicoes().size()
			  );

			  List<PraiaDTO> dtos = praiasCondicoes.stream()
			      .map(PraiaMapper::toDTO)
			      .collect(Collectors.toList());

			  return new PageImpl<>(dtos); // ou com paginação, como explicado antes
			}

		return pagePraias.map(this::buildDetalhesPraiaDTO);

	}

	public PraiaDTO pesquisarPraiasId(Long praiaId) throws FlopException {
		Praia praia = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Esta praia não foi encontrada!", HttpStatus.NOT_FOUND));

		return buildDetalhesPraiaDTO(praia);
	}

	/** Constrói o DTO com avaliações, postagens, imagens e contagens */
	private PraiaDTO buildDetalhesPraiaDTO(Praia praia) {
		PraiaDTO dto = PraiaMapper.toDTO(praia);
		Long id = praia.getIdPraia();

		// 1) Avaliações
		List<Avaliacao> avaliacoes = avaliacaoRepository.findByPraia_IdPraia(id);

		// **seta o total de avaliações do dia**
		dto.setTotalAvaliacoesDoDia(avaliacoes.size());

		// contagem por condição
		Map<String, Integer> condicoes = contarCondicoesAvaliacoes(avaliacoes);
		dto.setCondicoesAvaliacoes(condicoes);

		// 2) Postagens
		List<Postagem> postagens = postagemRepository.findByPraia_IdPraia(id);
		dto.setMensagensPostagens(postagens.stream().map(Postagem::getMensagem).collect(Collectors.toList()));
		dto.setImagensPostagens(
				postagens.stream().map(Postagem::getImagem).filter(Objects::nonNull).collect(Collectors.toList()));

		return dto;
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

	public Praia editarPraia(PraiaDTO praiaEditadaDto, Long praiaId) throws FlopException {
		Praia existente = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));

		existente.setNomePraia(praiaEditadaDto.getNomePraia());
		existente.setImagem(praiaEditadaDto.getImagem());
		existente.setLatitude(praiaEditadaDto.getLatitude());
		existente.setLongitude(praiaEditadaDto.getLongitude());
		existente.setPlaceId(praiaEditadaDto.getPlaceId());

		return praiaRepository.save(existente);
	}

	public List<Avaliacao> buscarAvaliacoesDoDia(Long praiaId) {
		LocalDate hoje = LocalDate.now();
		LocalDateTime inicioDoDia = hoje.atStartOfDay(); // Início do dia (00:00:00)
		LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX); // Fim do dia (23:59:59.999999999)

		return avaliacaoRepository.findAvaliacoesDoDia(praiaId, inicioDoDia, fimDoDia);
	}

	private static Map<String, Integer> contarCondicoesAvaliacoes(List<Avaliacao> avaliacoes) {
		Map<String, Integer> condicoesContagem = new HashMap<>();
		for (Avaliacao avaliacao : avaliacoes) {
			for (Condicoes condicao : avaliacao.getCondicoes()) {
				String condicaoStr = condicao.toString();
				condicoesContagem.put(condicaoStr, condicoesContagem.getOrDefault(condicaoStr, 0) + 1);
			}
		}
		return condicoesContagem;
	}

	public PraiaDTO obterInformacoesPraiaHoje(Long praiaId) throws FlopException {
		Praia praia = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));
		PraiaDTO dto = PraiaMapper.toDTO(praia);

		List<Avaliacao> avaliacoesDoDia = buscarAvaliacoesDoDia(praiaId);
		dto.setTotalAvaliacoesDoDia(avaliacoesDoDia.size());
		Map<String, Integer> condicoesContagem = contarCondicoesAvaliacoes(avaliacoesDoDia);
		dto.setCondicoesAvaliacoes(condicoesContagem);

		return dto;
	}
}
