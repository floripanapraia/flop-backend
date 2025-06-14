package com.vitalu.flop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.PostagemDTO;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.PostagemSeletor;

@Service
public class PostagemService {

	@Autowired
	private PostagemRepository postagemRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PraiaRepository praiaRepository;
	@Autowired
	private ImagemService imagemService;
	@Autowired
	private GeminiService geminiService;

	public PostagemDTO cadastrar(PostagemDTO postagemDTO) throws FlopException {
		Optional<Usuario> autor = usuarioRepository.findById(postagemDTO.getUsuarioId());
		Usuario usuario = autor.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST));

		Optional<Praia> praia = praiaRepository.findById(postagemDTO.getPraiaId());
		Praia praiaCadastrada = praia
				.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.BAD_REQUEST));

		Postagem postagem = new Postagem();
		postagem.setUsuario(usuario);
		postagem.setPraia(praiaCadastrada);
		postagem.setCriadoEm(LocalDateTime.now());
		postagem.setImagem(postagemDTO.getImagem());
		postagem.setMensagem(postagemDTO.getMensagem());
		postagem.setExcluida(false);

		postagemRepository.save(postagem);
		return Postagem.toDTO(postagem);
	}

	// uma mensagem deve ser excluída apenas logicamente, permitido apenas para
	// usuário ADMIN ou para USUARIO que criou a postagem
	public void excluir(Long idPostagem, Long idUsuario) throws FlopException {
		Postagem postagem = postagemRepository.findById(idPostagem).orElseThrow(
				() -> new FlopException("A postagem selecionada não foi encontrada.", HttpStatus.BAD_REQUEST));
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		boolean isAdmin = usuario.isAdmin();
		boolean isOwner = postagem.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

		if (!isAdmin && !isOwner) {
			throw new FlopException("Você não é o dono desta postagem, portanto não pode excluí-la.",
					HttpStatus.FORBIDDEN);
		}

		postagemRepository.save(postagem);
	}

	public void excluirPostagensDoUsuario(Long idUsuario) {
		List<Postagem> postagens = postagemRepository.findByUsuarioIdUsuario(idUsuario);
		postagemRepository.deleteAll(postagens);
	}

	public List<PostagemDTO> pesquisarTodos() {
		List<Postagem> postagens = postagemRepository.findAll();
		return postagens.stream().map(Postagem::toDTO).toList();
	}

	public PostagemDTO pesquisarPorId(Long id) throws FlopException {
		Postagem postagem = postagemRepository.findById(id)
				.orElseThrow(() -> new FlopException("A postagem buscada não foi encontrada.", HttpStatus.BAD_REQUEST));
		return Postagem.toDTO(postagem);
	}

	public List<PostagemDTO> pesquisarComFiltros(PostagemSeletor seletor) throws FlopException {
		List<Postagem> postagensFiltradas;

		if (seletor.temPaginacao()) {
			int pageNumber = seletor.getPagina();
			int pageSize = seletor.getLimite();

			PageRequest page = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "criadoEm"));
			postagensFiltradas = new ArrayList<Postagem>(postagemRepository.findAll(seletor, page).toList());
		} else {
			postagensFiltradas = new ArrayList<Postagem>(
					postagemRepository.findAll(seletor, Sort.by(Sort.Direction.DESC, "criadoEm")));
		}

		return postagensFiltradas.stream().map(Postagem::toDTO).collect(Collectors.toList());
	}

	public void salvarImagem(MultipartFile foto, Long idPostagem, Long idUsuario) throws FlopException {
		Postagem postagem = postagemRepository.findById(idPostagem)
				.orElseThrow(() -> new FlopException("Postagem não encontrada.", HttpStatus.NOT_FOUND));

		if (!postagem.getUsuario().getIdUsuario().equals(idUsuario)) {
			throw new FlopException("Você não tem permissão para fazer esta ação.", HttpStatus.FORBIDDEN);
		}

		String imagemBase64 = imagemService.processarImagem(foto);
		postagem.setImagem(imagemBase64);
		postagemRepository.save(postagem);
	}

	public int contarPaginas(PostagemSeletor seletor) {
		if (seletor != null && seletor.temPaginacao()) {
			int pageSize = seletor.getLimite();
			PageRequest pagina = PageRequest.of(0, pageSize); // Página inicial apenas para contar

			Page<Postagem> paginaResultado = postagemRepository.findAll(seletor, pagina);
			return paginaResultado.getTotalPages(); // Retorna o número total de páginas
		}

		// Se não houver paginação, retorna 1 página se houver registros, ou 0 se não
		// houver registros.
		long totalRegistros = postagemRepository.count(seletor);
		return totalRegistros > 0 ? 1 : 0;
	}

}
