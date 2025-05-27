package com.vitalu.flop.service;

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
import com.vitalu.flop.model.dto.AvaliacaoDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.AvaliacaoSeletor;

@Service
public class AvaliacaoService {

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PraiaRepository praiaRepository;

	public AvaliacaoDTO cadastrar(AvaliacaoDTO avaliacaoDTO) throws FlopException {

		if (avaliacaoDTO == null) {
			throw new FlopException("Dados da avaliação inválidos.", HttpStatus.BAD_REQUEST);
		}

		Avaliacao avaliacao = new Avaliacao();

		Usuario usuario = usuarioRepository.findById(avaliacaoDTO.getIdUsuario())
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST));

		Praia praia = praiaRepository.findById(avaliacaoDTO.getIdPraia())
				.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND));

		avaliacao.setUsuario(usuario);
		avaliacao.setPraia(praia);
		avaliacao.setCondicoes(avaliacaoDTO.getCondicoes());

		validarCondicoes(avaliacaoDTO.getCondicoes());
		Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
		return Avaliacao.toDTO(avaliacaoSalva);
	}

	public AvaliacaoDTO buscarPorId(Long idAvaliacao) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação buscada não foi encontrada.", HttpStatus.NOT_FOUND));

		AvaliacaoDTO dto = Avaliacao.toDTO(avaliacao);
		return dto;
	}

	public AvaliacaoDTO atualizar(Long idAvaliacao, AvaliacaoDTO editarAvaliacaoDTO) throws FlopException {

		Avaliacao avaliacaoExistente = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("Avaliação não encontrada.", HttpStatus.NOT_FOUND));

		if (editarAvaliacaoDTO.getCondicoes() != null && !editarAvaliacaoDTO.getCondicoes().isEmpty()) {
			validarCondicoes(editarAvaliacaoDTO.getCondicoes());
			avaliacaoExistente.setCondicoes(editarAvaliacaoDTO.getCondicoes());
		}

		Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacaoExistente);
		return Avaliacao.toDTO(avaliacaoAtualizada);
	}

	public void excluir(Long idAvaliacao, Long idUsuario) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao).orElseThrow(
				() -> new FlopException("A avaliação selecionada não foi encontrada.", HttpStatus.BAD_REQUEST));
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		boolean isAdmin = usuario.isAdmin();
		boolean isOwner = avaliacao.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

		if (!isAdmin && !isOwner) {
			throw new FlopException("Você não é o Dono desta avaliação, portanto não pode excluí-la.",
					HttpStatus.FORBIDDEN);
		}
		avaliacaoRepository.delete(avaliacao);
	}

	private void validarCondicoes(List<Condicoes> condicoes) throws FlopException {
		// Mapeamento de condições incompatíveis
		Map<Condicoes, List<Condicoes>> condicoesIncompativeis = Map.of(Condicoes.SOL,
				List.of(Condicoes.CHUVA, Condicoes.NUBLADO), Condicoes.CHUVA, List.of(Condicoes.SOL), Condicoes.NUBLADO,
				List.of(Condicoes.SOL), Condicoes.MAR_CALMO, List.of(Condicoes.MAR_ONDAS), Condicoes.MAR_ONDAS,
				List.of(Condicoes.MAR_CALMO), Condicoes.LIMPA, List.of(Condicoes.LIXO), Condicoes.LIXO,
				List.of(Condicoes.LIMPA)

		);

		// Verifica se alguma condição selecionada entra em conflito com outra
		for (Condicoes condicao : condicoes) {
			List<Condicoes> conflitantes = condicoesIncompativeis.get(condicao);
			if (conflitantes != null && condicoes.stream().anyMatch(conflitantes::contains)) {
				throw new FlopException("As condições selecionadas são incompatíveis: " + condicao
						+ " não pode estar junto com " + conflitantes.stream().filter(condicoes::contains).toList(),
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	public Page<AvaliacaoDTO> pesquisarComFiltros(AvaliacaoSeletor seletor) throws FlopException {
		Pageable pageable = Pageable.unpaged();

		if (seletor.temPaginacao()) {
			pageable = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("criadoEm").ascending());
		}

		Page<Avaliacao> avaliacoesFiltradas = avaliacaoRepository.findAll(seletor, pageable);

		return avaliacoesFiltradas.map(Avaliacao::toDTO);
	}

}
