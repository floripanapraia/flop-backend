package com.vitalu.flop.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class AvaliacaoService {

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PraiaRepository praiaRepository;

	public void cadastrar(Long idUsuario, Long idPraia, List<Condicoes> condicoes) throws FlopException {
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		Praia praia = praiaRepository.findById(idPraia)
				.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND));

		validarCondicoes(condicoes);

		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setUsuario(usuario);
		avaliacao.setPraia(praia);
		avaliacao.setCondicoes(condicoes);

		avaliacaoRepository.save(avaliacao);
	}

	public Avaliacao buscarPorId(Long idAvaliacao) throws FlopException {
		return avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação buscada não foi encontrada.", HttpStatus.NOT_FOUND));
	}

	public void atualizar(Long idAvaliacao, List<Condicoes> condicoes) throws FlopException {
		Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(idAvaliacao);

		if (avaliacaoOptional.isEmpty()) {
			throw new FlopException("Avaliação não encontrada.", HttpStatus.NOT_FOUND);
		}

		Avaliacao avaliacao = avaliacaoOptional.get();

		// Validação de condições
		if (condicoes == null || condicoes.isEmpty()) {
			throw new FlopException("É necessário selecionar ao menos uma condição.", HttpStatus.BAD_REQUEST);
		}

		validarCondicoes(condicoes);

		avaliacao.setCondicoes(condicoes);
		avaliacaoRepository.save(avaliacao);
	}

	public void excluir(Long idAvaliacao) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação não foi encontrada.", HttpStatus.NOT_FOUND));

		avaliacaoRepository.delete(avaliacao);

		// Verificar se a avaliação foi excluída
		if (avaliacaoRepository.existsById(idAvaliacao)) {
			throw new FlopException("A avaliação não pôde ser excluída.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

	// seletor
//	public List<Avaliacao> listarAvaliacoesPorPraia(Praia praia) {
//		return avaliacaoRepository.findByPraia(praia);
//	}
//
//	public List<Avaliacao> listarAvaliacoesPorUsuario(Usuario usuario) {
//		return avaliacaoRepository.findByUsuario(usuario);
//	}

}
