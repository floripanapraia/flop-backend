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
import com.vitalu.flop.model.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {
	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

//	@Autowired
//	private UsuarioService usuarioService; 
//
//	@Autowired
//	private PraiaService praiaService; 

	public Avaliacao cadastrar(Avaliacao avaliacao) throws FlopException {
		// verificar se usuario existe
		Optional<Usuario> usuario = Optional.ofNullable(avaliacao.getUsuario());
		usuario.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST));

		// verificar se a praia existe
		Optional<Praia> praia = Optional.ofNullable(avaliacao.getPraia());
		praia.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.BAD_REQUEST));

		return avaliacaoRepository.save(avaliacao);
	}

	public Avaliacao buscarPorId(Long idAvaliacao) throws FlopException {
		return avaliacaoRepository.findById(idAvaliacao).orElseThrow(
				() -> new FlopException("A avaliação buscada não foi encontrada.", HttpStatus.BAD_REQUEST));
	}

	public List<Avaliacao> listarAvaliacoesPorPraia(Praia praia) {
		return avaliacaoRepository.findByPraia(praia);
	}

	public List<Avaliacao> listarAvaliacoesPorUsuario(Usuario usuario) {
		return avaliacaoRepository.findByUsuario(usuario);
	}

	public Avaliacao atualizar(Long idAvaliacao, Avaliacao avaliacaoAtualizada) throws FlopException {
		Avaliacao avaliacaoExistente = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação não foi encontrada.", HttpStatus.BAD_REQUEST));

		// Atualizar os dados da avaliação
		avaliacaoExistente.setCondicoes(avaliacaoAtualizada.getCondicoes());
		avaliacaoExistente.setPraia(avaliacaoAtualizada.getPraia());
		avaliacaoExistente.setUsuario(avaliacaoAtualizada.getUsuario());

		return avaliacaoRepository.save(avaliacaoExistente);
	}

	public void excluir(Long idAvaliacao) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação não foi encontrada.", HttpStatus.BAD_REQUEST));

		avaliacaoRepository.delete(avaliacao);
	}

}
