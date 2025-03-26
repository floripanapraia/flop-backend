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

	public Avaliacao cadastrar(Avaliacao avaliacao) throws FlopException {
		Optional<Usuario> usuario = usuarioRepository.findById(avaliacao.getUsuario().getIdUsuario());
		avaliacao.setUsuario(
				usuario.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST)));

		Optional<Praia> praia = praiaRepository.findById(avaliacao.getPraia().getIdPraia());
		avaliacao.setPraia(praia.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND)));

		validarCondicoes(avaliacao.getCondicoes());

		return avaliacaoRepository.save(avaliacao);
	}

	public Avaliacao buscarPorId(Long idAvaliacao) throws FlopException {
		return avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação buscada não foi encontrada.", HttpStatus.NOT_FOUND));
	}

	public Avaliacao atualizar(Long idAvaliacao, Avaliacao editarAvaliacao) throws FlopException {
		Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(idAvaliacao);

		if (avaliacaoOptional.isEmpty()) {
			throw new FlopException("Avaliação não encontrada.", HttpStatus.NOT_FOUND);
		}

		Avaliacao avaliacaoExistente = avaliacaoOptional.get();

		// Atualiza as condições
		if (editarAvaliacao.getCondicoes() != null && !editarAvaliacao.getCondicoes().isEmpty()) {
			validarCondicoes(editarAvaliacao.getCondicoes()); // Valida as condições
			avaliacaoExistente.setCondicoes(editarAvaliacao.getCondicoes()); // Atualiza as condições
		}

		return avaliacaoRepository.save(avaliacaoExistente);
	}

	// uma mensagem deve ser excluída apenas logicamente, permitido apenas para
	// usuário ADMIN ou para USUARIO que criou a avaliacao
	public void excluir(Long idAvaliacao, Long idUsuario) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("O pruu selecionado não foi encontrado.", HttpStatus.BAD_REQUEST));
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		boolean isAdmin = usuario.isAdmin();
		boolean isOwner = avaliacao.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

		if (!isAdmin && !isOwner) {
			throw new FlopException("Você não é o Pombo dono deste Pruu, portanto não pode excluí-lo.",
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

	// seletor
//	public List<Avaliacao> listarAvaliacoesPorPraia(Praia praia) {
//		return avaliacaoRepository.findByPraia(praia);
//	}
//
//	public List<Avaliacao> listarAvaliacoesPorUsuario(Usuario usuario) {
//		return avaliacaoRepository.findByUsuario(usuario);
//	}

}
