package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.CriarPostagemDTO;
import com.vitalu.flop.model.dto.PostagemDTO;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.mock.PostagemMockFactory;
import com.vitalu.flop.model.mock.PraiaMockFactory;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.PostagemSeletor;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para PostagemService")
class PostagemServiceTest {

	@InjectMocks
	private PostagemService postagemService;

	@Mock
	private PostagemRepository postagemRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	// Mesmo não usados em todos os métodos, são necessários pra injeção de
	// dependência
	@Mock
	private PraiaRepository praiaRepository;
	@Mock
	private ImagemService imagemService;
	@Mock
	private LocalizacaoService localizacaoService;
	@Mock
	private GeminiService geminiService; // Mock para o novo serviço de IA

	private Postagem postagemValida;
	private Usuario usuarioDono;
	private Usuario usuarioAdmin;

	private CriarPostagemDTO postagemDTOValida;
	private Usuario usuarioValido;
	private Praia praiaValida;

	@BeforeEach
	void setUp() {
		postagemValida = PostagemMockFactory.criarPostagemPadrao();
		usuarioDono = UsuarioMockFactory.criarUsuarioPadrao(); // ID 1L, admin=false
		usuarioAdmin = UsuarioMockFactory.criarUsuarioAdmin(); // ID 1L, mas vamos mudar para 2L para diferenciar
		usuarioAdmin.setIdUsuario(2L);

		// Constrói específicamente para o teste de cadastro
		usuarioValido = UsuarioMockFactory.criarUsuarioPadrao();
		praiaValida = PraiaMockFactory.criarPraiaPadrao();

		// Usa o construtor vazio e os setters para maior clareza
		postagemDTOValida = new CriarPostagemDTO();
		postagemDTOValida.setUsuarioId(1L); // Campo herdado de PostagemDTO
		postagemDTOValida.setPraiaId(1L); // Campo herdado de PostagemDTO
		postagemDTOValida.setMensagem("Que dia lindo na praia hoje!");
		postagemDTOValida.setImagem(null);
		postagemDTOValida.setLatitudeUser(-22.9711); // Campo de CriarPostagemDTO
		postagemDTOValida.setLongitudeUser(-43.1822); // Campo de CriarPostagemDTO
	}

	// Testes para o método cadastrar()
	@Test
	@DisplayName("Deve cadastrar a postagem quando todos os dados são válidos")
	void testCadastrar_ComDadosValidos_DeveRetornarPostagemDTO() throws FlopException {
		// Arrange (Configuração)
		// Simula o comportamento esperado dos mocks para um cenário de sucesso
		when(geminiService.isMensagemConsideradaOfensiva(anyString())).thenReturn(false);
		when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioValido));
		when(praiaRepository.findById(anyLong())).thenReturn(Optional.of(praiaValida));
		// Garante que a validação de proximidade não lance exceção
		doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());
		// Quando o repositório salvar, ele deve retornar a postagem
		when(postagemRepository.save(any(Postagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act (Ação)
		PostagemDTO resultado = postagemService.cadastrar(postagemDTOValida);

		// Assert (Verificação)
		assertNotNull(resultado);
		assertEquals(postagemDTOValida.getMensagem(), resultado.getMensagem());
		assertEquals(usuarioValido.getNickname(), resultado.getNickname());
		// Garante que o método save foi chamado exatamente uma vez
		verify(postagemRepository, times(1)).save(any(Postagem.class));
	}

	@Test
	@DisplayName("Deve lançar exceção se a mensagem contiver URL")
	void testCadastrar_ComMensagemContendoUrl_DeveLancarExcecao() {
		// Arrange
		postagemDTOValida.setMensagem("Visite www.meusite.com");

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> postagemService.cadastrar(postagemDTOValida));
		assertEquals("Sua mensagem parece conter links, palavras impróprias ou dados pessoais, que não são permitidos.",
				exception.getMessage());
		verify(postagemRepository, never()).save(any()); // Verifica que não tentou salvar
	}

	@Test
	@DisplayName("Deve lançar exceção se a mensagem contiver palavra proibida")
	void testCadastrar_ComMensagemContendoPalavraProibida_DeveLancarExcecao() {
		// Arrange
		postagemDTOValida.setMensagem("Que dia de merda!");

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> postagemService.cadastrar(postagemDTOValida));
		assertEquals("Sua mensagem parece conter links, palavras impróprias ou dados pessoais, que não são permitidos.",
				exception.getMessage());
	}

	@Test
	@DisplayName("Deve lançar exceção se o Gemini considerar a mensagem ofensiva")
	void testCadastrar_QuandoGeminiConsideraOfensiva_DeveLancarExcecao() throws FlopException {
		// Arrange
		when(geminiService.isMensagemConsideradaOfensiva(anyString())).thenReturn(true);

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> postagemService.cadastrar(postagemDTOValida));
		assertEquals("Sua mensagem foi considerada imprópria ou excessivamente negativa pela moderação.",
				exception.getMessage());
	}

	@Test
	@DisplayName("Deve lançar exceção se as coordenadas do usuário forem nulas")
	void testCadastrar_ComCoordenadasNulas_DeveLancarExcecao() throws FlopException {
		// Arrange
		postagemDTOValida.setLatitudeUser(null);
		postagemDTOValida.setLongitudeUser(null);
		when(geminiService.isMensagemConsideradaOfensiva(anyString())).thenReturn(false);

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> postagemService.cadastrar(postagemDTOValida));
		assertEquals(
				"É necessário permitir o acesso à sua localização. Sem as coordenadas do usuário, não será possível postar na praia.",
				exception.getMessage());
	}

	// Testes para o método excluir()
	@Test
	@DisplayName("Deve marcar postagem como excluída quando o usuário é o dono")
	void testExcluir_QuandoUsuarioEhDono_DeveMarcarComoExcluida() throws FlopException {
		// Arrange
		when(postagemRepository.findById(postagemValida.getIdPostagem())).thenReturn(Optional.of(postagemValida));
		when(usuarioRepository.findById(usuarioDono.getIdUsuario())).thenReturn(Optional.of(usuarioDono));

		// Act
		postagemService.excluir(postagemValida.getIdPostagem(), usuarioDono.getIdUsuario());

		// Assert
		ArgumentCaptor<Postagem> postagemCaptor = ArgumentCaptor.forClass(Postagem.class);
		verify(postagemRepository).save(postagemCaptor.capture());

		Postagem postagemSalva = postagemCaptor.getValue();
		assertTrue(postagemSalva.getExcluida());
	}

	@Test
	@DisplayName("Deve marcar postagem como excluída quando o usuário é admin")
	void testExcluir_QuandoUsuarioEhAdmin_DeveMarcarComoExcluida() throws FlopException {
		// Arrange
		when(postagemRepository.findById(postagemValida.getIdPostagem())).thenReturn(Optional.of(postagemValida));
		when(usuarioRepository.findById(usuarioAdmin.getIdUsuario())).thenReturn(Optional.of(usuarioAdmin));

		// Act
		postagemService.excluir(postagemValida.getIdPostagem(), usuarioAdmin.getIdUsuario());

		// Assert
		ArgumentCaptor<Postagem> postagemCaptor = ArgumentCaptor.forClass(Postagem.class);
		verify(postagemRepository).save(postagemCaptor.capture());

		Postagem postagemSalva = postagemCaptor.getValue();
		assertTrue(postagemSalva.getExcluida());
	}

	@Test
	@DisplayName("Deve lançar FlopException ao tentar excluir postagem de outro usuário sem ser admin")
	void testExcluir_QuandoUsuarioNaoAutorizado_DeveLancarExcecao() {
		// Arrange
		Usuario usuarioNaoAutorizado = UsuarioMockFactory.criarUsuarioPadrao();
		usuarioNaoAutorizado.setIdUsuario(3L);

		when(postagemRepository.findById(anyLong())).thenReturn(Optional.of(postagemValida));
		when(usuarioRepository.findById(usuarioNaoAutorizado.getIdUsuario()))
				.thenReturn(Optional.of(usuarioNaoAutorizado));

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> {
			postagemService.excluir(postagemValida.getIdPostagem(), usuarioNaoAutorizado.getIdUsuario());
		});

		assertEquals("Você não é o dono desta postagem, portanto não pode excluí-la.", exception.getMessage());
		assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
		verify(postagemRepository, never()).save(any());
	}

	@Test
	@DisplayName("Deve lançar FlopException ao tentar excluir postagem inexistente")
	void testExcluir_QuandoPostagemNaoEncontrada_DeveLancarExcecao() {
		// Arrange
		when(postagemRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(FlopException.class, () -> {
			postagemService.excluir(99L, usuarioDono.getIdUsuario());
		});
	}

	// Testes para o método pesquisarPorId()
	@Test
	@DisplayName("Deve retornar PostagemDTO ao pesquisar por ID existente")
	void testPesquisarPorId_QuandoIdExiste_DeveRetornarDTO() throws FlopException {
		// Arrange
		when(postagemRepository.findById(postagemValida.getIdPostagem())).thenReturn(Optional.of(postagemValida));

		// Act
		PostagemDTO resultado = postagemService.pesquisarPorId(postagemValida.getIdPostagem());

		// Assert
		assertNotNull(resultado);
		assertEquals(postagemValida.getIdPostagem(), resultado.getIdPostagem());
		assertEquals(postagemValida.getMensagem(), resultado.getMensagem());
	}

	@Test
	@DisplayName("Deve lançar FlopException ao pesquisar por ID inexistente")
	void testPesquisarPorId_QuandoIdNaoExiste_DeveLancarExcecao() {
		// Arrange
		when(postagemRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> {
			postagemService.pesquisarPorId(99L);
		});
		assertEquals("A postagem buscada não foi encontrada.", exception.getMessage());
	}

	// Testes para salvarImagem
	@Test
	@DisplayName("Deve salvar a imagem na postagem com sucesso")
	void testSalvarImagem_ComUsuarioAutorizado_DeveSalvarImagem() throws FlopException {
		// Arrange
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
				"some-image-bytes".getBytes());
		String base64Image = "data:image/jpeg;base64,c29tZS1pbWFnZS1ieXRlcw==";

		when(postagemRepository.findById(postagemValida.getIdPostagem())).thenReturn(Optional.of(postagemValida));
		when(imagemService.processarImagem(mockFile)).thenReturn(base64Image);

		// Act
		postagemService.salvarImagem(mockFile, postagemValida.getIdPostagem(), usuarioDono.getIdUsuario());

		// Assert
		ArgumentCaptor<Postagem> postagemCaptor = ArgumentCaptor.forClass(Postagem.class);
		verify(postagemRepository).save(postagemCaptor.capture());

		Postagem postagemSalva = postagemCaptor.getValue();
		assertEquals(base64Image, postagemSalva.getImagem());
	}

	@Test
	@DisplayName("Deve lançar FlopException ao tentar salvar imagem sem permissão")
	void testSalvarImagem_ComUsuarioNaoAutorizado_DeveLancarExcecao() {
		// Arrange
		MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
				"some-image-bytes".getBytes());
		Long idUsuarioNaoAutorizado = 99L;

		when(postagemRepository.findById(postagemValida.getIdPostagem())).thenReturn(Optional.of(postagemValida));

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> {
			postagemService.salvarImagem(mockFile, postagemValida.getIdPostagem(), idUsuarioNaoAutorizado);
		});

		assertEquals("Você não tem permissão para fazer esta ação.", exception.getMessage());
		assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
		verify(postagemRepository, never()).save(any());
	}

	// Testes para contarPaginas
	@Test
	@DisplayName("Deve retornar o número total de páginas quando há paginação")
	void testContarPaginas_ComPaginacao_DeveRetornarTotalDePaginas() {
		// Arrange
		PostagemSeletor seletor = new PostagemSeletor();
		seletor.setLimite(10);
		seletor.setPagina(1);

		Page<Postagem> pageMock = mock(Page.class);
		when(pageMock.getTotalPages()).thenReturn(5);
		when(postagemRepository.findAll(eq(seletor), any(PageRequest.class))).thenReturn(pageMock);

		// Act
		int totalPaginas = postagemService.contarPaginas(seletor);

		// Assert
		assertEquals(5, totalPaginas);
	}

	@Test
	@DisplayName("Deve retornar 1 quando não há paginação mas existem registros")
	void testContarPaginas_SemPaginacaoComRegistros_DeveRetornarUm() {
		// Arrange
		PostagemSeletor seletor = new PostagemSeletor(); // Sem limite e página
		when(postagemRepository.count(seletor)).thenReturn(15L);

		// Act
		int totalPaginas = postagemService.contarPaginas(seletor);

		// Assert
		assertEquals(1, totalPaginas);
	}

	@Test
	@DisplayName("Deve retornar 0 quando não há paginação e nem registros")
	void testContarPaginas_SemPaginacaoSemRegistros_DeveRetornarZero() {
		// Arrange
		PostagemSeletor seletor = new PostagemSeletor();
		when(postagemRepository.count(seletor)).thenReturn(0L);

		// Act
		int totalPaginas = postagemService.contarPaginas(seletor);

		// Assert
		assertEquals(0, totalPaginas);
	}

}