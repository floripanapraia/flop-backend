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
import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.dto.SugestaoDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Localizacao;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.LocalizacaoRepository;
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
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private PostagemRepository postagemRepository;

	public Praia cadastrarPraia(PraiaDTO dto) throws FlopException {

		Localizacao local = localizacaoRepository.save(dto.getLocalizacao());

		Praia novaPraia = new Praia();
		novaPraia.setNomePraia(dto.getNomePraia());
		novaPraia.setLocalizacao(local);

		return praiaRepository.save(novaPraia);
	}

	public void excluirPraia(Long praiaId) throws FlopException {
	    Praia praia = praiaRepository.findById(praiaId)
	            .orElseThrow(() -> new FlopException("Praia não encontrada", HttpStatus.NOT_FOUND));
	    
	    // Exclui a localização associada se existir
	    if (praia.getLocalizacao() != null) {
	        localizacaoRepository.deleteById(praia.getLocalizacao().getIdLocalizacao());
	    }
	    
	    praiaRepository.deleteById(praiaId);
	}
	
	public List<PraiaDTO> pesquisarPraiaTodas() throws FlopException {
		List<Praia> praias = praiaRepository.findAll();
		List<PraiaDTO> praiasDTO = new ArrayList<PraiaDTO>();

		for (Praia praia : praias) {
			PraiaDTO dto = converterParaDTO(praia);
			praiasDTO.add(dto);
		}
		return praiasDTO;
	}

	public PraiaDTO pesquisarPraiasId(Long praiaId) throws FlopException {
		Praia sugestao = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Esta praia não foi encontrada!", HttpStatus.NOT_FOUND));

		PraiaDTO dto = converterParaDTO(sugestao);
		return dto;
	}

	public Page<PraiaDTO> pesquisarPraiaFiltros(PraiaSeletor seletor) throws FlopException {
	    Pageable pageable = Pageable.unpaged();

	    if (seletor.temPaginacao()) {
	        pageable = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("criadaEm").ascending());
	    }

	    Page<Praia> praias = praiaRepository.findAll(seletor, pageable);

	    Page<PraiaDTO> praiasDTO = praias.map(praia -> {
	        PraiaDTO dto = converterParaDTO(praia);
	        return dto;
	    });

	    return praiasDTO;
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

	public Praia editarPraia(PraiaDTO praiaEditadaDto, Long usuarioId) throws FlopException {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new FlopException("Usuário não localizado.", HttpStatus.NOT_FOUND));

		if (!usuario.isAdmin()) {
			throw new FlopException("Apenas administradores podem editar praias!", HttpStatus.UNAUTHORIZED);
		}

		Praia existente = praiaRepository.findById(praiaEditadaDto.getIdPraia())
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));

		existente.setNomePraia(praiaEditadaDto.getNomePraia());
		existente.setImagem(praiaEditadaDto.getImagem());

		// Atualizar a localização se for fornecida no DTO
		if (praiaEditadaDto.getLocalizacao().getIdLocalizacao() != null) {
			Localizacao novaLocalizacao = localizacaoRepository
					.findById(praiaEditadaDto.getLocalizacao().getIdLocalizacao())
					.orElseThrow(() -> new FlopException("Localização não localizada.", HttpStatus.NOT_FOUND));
			existente.setLocalizacao(novaLocalizacao);
		}

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

	private Map<String, Integer> contarCondicoesAvaliacoes(List<Avaliacao> avaliacoes) {
		// Conta cada condição adicionando num Map CONDICAO-QUANTIDADE
		Map<String, Integer> condicoesContagem = new HashMap<>();
		for (Avaliacao avaliacao : avaliacoes) {
			for (Condicoes condicao : avaliacao.getCondicoes()) {
				String condicaoStr = condicao.toString();
				condicoesContagem.put(condicaoStr, condicoesContagem.getOrDefault(condicaoStr, 0) + 1);
			}
		}
		return condicoesContagem;
	}

	private PraiaDTO converterParaDTO(Praia praia) {
		PraiaDTO dto = new PraiaDTO();
		dto.setIdPraia(praia.getIdPraia());
		dto.setNomePraia(praia.getNomePraia());
		dto.setImagem(praia.getImagem());
		dto.setLocalizacao(praia.getLocalizacao() != null ? praia.getLocalizacao() : null);

		// Contando as condições das avaliações do dia
		List<Avaliacao> avaliacoes = buscarAvaliacoesDoDia(praia.getIdPraia());
		Map<String, Integer> condicoesContagem = contarCondicoesAvaliacoes(avaliacoes);
		dto.setCondicoesAvaliacoes(condicoesContagem);

		// Adicionando as postagens e imagens do dia
		List<String> mensagensPostagens = new ArrayList<>();
		List<String> imagensPostagens = new ArrayList<>();
		List<Postagem> postagens = buscarPostagensDoDia(praia.getIdPraia());
		for (Postagem postagem : postagens) {
			mensagensPostagens.add(postagem.getMensagem());
			if (postagem.getImagem() != null) {
				imagensPostagens.add(postagem.getImagem());
			}
		}
		dto.setMensagensPostagens(mensagensPostagens);
		dto.setImagensPostagens(imagensPostagens);

		return dto;
	}

	public PraiaDTO obterInformacoesPraiaHoje(Long praiaId) throws FlopException {
		Praia praia = praiaRepository.findById(praiaId)
				.orElseThrow(() -> new FlopException("Praia não localizada.", HttpStatus.NOT_FOUND));

		PraiaDTO praiaHojeDTO = converterParaDTO(praia);

		return praiaHojeDTO;
	}

	// TODO MÉTODOS DE IA

}
