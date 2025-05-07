package com.vitalu.flop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
		List<PraiaDTO> praiasDTO = new ArrayList<PraiaDTO>();

		for (Praia praia : praias) {
			PraiaDTO dto = PraiaMapper.toDTO(praia);
			praiasDTO.add(dto);
		}
		return praiasDTO;
	}

	public PraiaDTO pesquisarPraiasId(Long praiaId) throws FlopException {
		Praia praia = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Esta praia não foi encontrada!", HttpStatus.NOT_FOUND));

		return PraiaMapper.toDTO(praia);
	}

	public Page<PraiaDTO> pesquisarPraiaFiltros(PraiaSeletor seletor) throws FlopException {
		Pageable pageable = Pageable.unpaged();

		if (seletor.temPaginacao()) {
			pageable = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("nomePraia").ascending());
		}

		Page<Praia> praias = praiaRepository.findAll(seletor, pageable);

		return praias.map(PraiaMapper::toDTO);
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

	public List<Postagem> buscarPostagensDoDia(Long praiaId) {
		LocalDate hoje = LocalDate.now();
		LocalDateTime inicioDoDia = hoje.atStartOfDay();
		LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX);

		return postagemRepository.findPostagensDoDia(praiaId, inicioDoDia, fimDoDia);
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
		Map<String, Integer> condicoesContagem = contarCondicoesAvaliacoes(avaliacoesDoDia);
	    dto.setCondicoesAvaliacoes(condicoesContagem);
		
	    List<Postagem> postagensDoDia = buscarPostagensDoDia(praiaId);
	    List<String> mensagensPostagens = new ArrayList<>();
	    List<String> imagensPostagens = new ArrayList<>();
	    for (Postagem postagem : postagensDoDia) {
	        mensagensPostagens.add(postagem.getMensagem());
	        if (postagem.getImagem() != null) {
	            imagensPostagens.add(postagem.getImagem());
	        }
	    }
	    dto.setMensagensPostagens(mensagensPostagens);
	    dto.setImagensPostagens(imagensPostagens);

	    return dto;
	}

	// TODO MÉTODOS DE IA

}
