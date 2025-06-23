package com.vitalu.flop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
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

	// Lista de padrões Regex para validação prévia.
	private static final List<Pattern> PADROES_INVALIDOS = List.of(
			// Regex para URLs (http, https, www)
			Pattern.compile("(?i)(https?://|www\\.)\\S+"),
			// Regex para endereços de e-mail
			Pattern.compile("(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b"),
			// Regex para números de telefone (formatos brasileiros e sequências longas de
			// dígitos)
			Pattern.compile("(?:\\(?\\d{2}\\)?\\s?)?(?:9\\d{4}|\\d{4})[-.\\s]?\\d{4}|\\d{8,}"),
			// Regex para CPF (XXX.XXX.XXX-XX)
			Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"),
			// Regex para CNPJ (XX.XXX.XXX/XXXX-XX)
			Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"));

	// Lista de palavras proibidas
	// Adicionar outras palavras proibidas conforme necessário
	private static final List<String> PALAVRAS_PROIBIDAS = Arrays.asList(
    "morrer", "imbecil", "idiota", "estupro", "assassinar", "violência",

    // Xingamentos e Palavrões
    "merda", "bosta", "caralho", "porra", "puta", "foder", "arrombado", "desgraçado",
    "escroto", "babaca", "otário", "vagabundo", "filho da puta",

    // Termos Discriminatórios
    "macaco", "crioulo", "negrada", // Racismo
    "traveco", // Homofobia/Transfobia
    "vadia", "piranha", // Misoginia
    "mongol", "aleijado", "retardado", // Capacitismo
);

	/**
	 * Valida a mensagem contra uma lista de padrões indesejados (links, telefones,
	 * etc.).
	 * 
	 * @param mensagem O texto da postagem.
	 * @return true se a mensagem contém um padrão inválido, false caso contrário.
	 */
	private boolean mensagemContemPadroesInvalidos(String mensagem) {
		for (Pattern pattern : PADROES_INVALIDOS) {
			if (pattern.matcher(mensagem).find()) {
				return true; // Encontrou um padrão inválido
			}
		}
		return false; // A mensagem está limpa
	}

	/**
	 * Valida a mensagem contra uma lista de palavras proibidas.
	 */
	private boolean mensagemContemPalavrasProibidas(String mensagem) {
		if (mensagem == null || mensagem.isBlank())
			return false;
		String mensagemLowerCase = mensagem.toLowerCase();
		for (String palavra : PALAVRAS_PROIBIDAS) {
			if (mensagemLowerCase.contains(palavra)) {
				return true;
			}
		}
		return false;
	}

	public PostagemDTO cadastrar(PostagemDTO postagemDTO) throws FlopException {
		// Validação PRÉVIA com Regex e Palavras-Chave
		if (mensagemContemPadroesInvalidos(postagemDTO.getMensagem()) || 
				mensagemContemPalavrasProibidas(postagemDTO.getMensagem())) {
			throw new FlopException(
					"Sua mensagem parece conter links, palavras impróprias ou dados pessoais, que não são permitidos.",
					HttpStatus.BAD_REQUEST);
		}

		// Validação do conteúdo com Gemini (só roda se a validação prévia passar)
		if (geminiService.isMensagemConsideradaOfensiva(postagemDTO.getMensagem())) {
			throw new FlopException("Sua mensagem foi considerada imprópria ou excessivamente negativa pela moderação.",
					HttpStatus.BAD_REQUEST);
		}

		// Lógica de negócio
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
