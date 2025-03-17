package com.vitalu.flop.service;

import java.util.List;
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

	public List<Avaliacao> listarAvaliacoesPorPraia(Praia praia) {
		return avaliacaoRepository.findByPraia(praia);
	}

	public List<Avaliacao> listarAvaliacoesPorUsuario(Usuario usuario) {
		return avaliacaoRepository.findByUsuario(usuario);
	}

	public void atualizar(Long idAvaliacao, List<Condicoes> condicoes) throws FlopException {
		Optional<Avaliacao> avaliacaoOptional = avaliacaoRepository.findById(idAvaliacao);

		if (avaliacaoOptional.isEmpty()) {
			throw new FlopException("Avaliação não encontrada.", HttpStatus.NOT_FOUND);
		}

		Avaliacao avaliacao = avaliacaoOptional.get();

		// Validação de condições (deve ser um conjunto válido)
		if (condicoes == null || condicoes.isEmpty()) {
			throw new FlopException("É necessário selecionar ao menos uma condição.", HttpStatus.BAD_REQUEST);
		}

		// Atualiza as condições
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

}
