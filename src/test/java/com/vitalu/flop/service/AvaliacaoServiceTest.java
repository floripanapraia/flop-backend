package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.AvaliacaoDTO;
import com.vitalu.flop.model.dto.CriarAvaliacaoDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.mock.AvaliacaoMockFactory;
import com.vitalu.flop.model.mock.PraiaMockFactory;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.AvaliacaoSeletor;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvaliacaoService - Testes")
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PraiaRepository praiaRepository;

    @Mock
    private AuthService authService;

    @Mock
    private LocalizacaoService localizacaoService;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Usuario usuarioMock;
    private Praia praiaMock;
    private CriarAvaliacaoDTO criarAvaliacaoDTO;
    private Avaliacao avaliacaoMock;

    @BeforeEach
    void setUp() {
        usuarioMock = UsuarioMockFactory.criarUsuarioPadrao();
        praiaMock = PraiaMockFactory.criarPraiaPadrao();
        avaliacaoMock = AvaliacaoMockFactory.criarAvaliacaoPadrao();

        criarAvaliacaoDTO = new CriarAvaliacaoDTO();
        criarAvaliacaoDTO.setIdUsuario(1L);
        criarAvaliacaoDTO.setIdPraia(1L);
        criarAvaliacaoDTO.setLatitudeUser(-27.5949);
        criarAvaliacaoDTO.setLongitudeUser(-48.5482);
        criarAvaliacaoDTO.setCondicoes(List.of(Condicoes.SOL, Condicoes.MAR_CALMO, Condicoes.LIMPA));
    }

    @Test
    @DisplayName("Deve cadastrar avaliação com sucesso")
    void deveCadastrarAvaliacaoComSucesso() throws FlopException {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacaoMock);
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When
        AvaliacaoDTO resultado = avaliacaoService.cadastrar(criarAvaliacaoDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(avaliacaoMock.getIdAvaliacao(), resultado.getIdAvaliacao());
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(localizacaoService).validarProximidadePraia(1L, -27.5949, -48.5482);
    }

    @Test
    @DisplayName("Deve lançar exceção quando latitude/longitude não fornecidas")
    void deveLancarExcecaoQuandoLatitudeLongitudeNaoFornecidas() {
        // Given
        criarAvaliacaoDTO.setLatitudeUser(null);
        criarAvaliacaoDTO.setLongitudeUser(null);

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertEquals("É necessário permitir o acesso à sua localização. Sem as coordenadas do usuário, não será possível avaliar a praia.", 
            exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertEquals("Usuário não encontrado.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando praia não encontrada")
    void deveLancarExcecaoQuandoPraiaNaoEncontrada() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertEquals("Praia não encontrada.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já avaliou a praia hoje")
    void deveLancarExcecaoQuandoUsuarioJaAvaliouPraiaHoje() throws FlopException {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(avaliacaoMock));
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertTrue(exception.getMessage().contains("Você já fez uma avaliação hoje para a praia"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção para condições incompatíveis")
    void deveLancarExcecaoParaCondicoesIncompativeis() throws FlopException {
        // Given
        criarAvaliacaoDTO.setCondicoes(List.of(Condicoes.SOL, Condicoes.CHUVA)); // Incompatíveis
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertTrue(exception.getMessage().contains("As condições selecionadas são incompatíveis"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Deve buscar avaliação por ID com sucesso")
    void deveBuscarAvaliacaoPorIdComSucesso() throws FlopException {
        // Given
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoMock));

        // When
        AvaliacaoDTO resultado = avaliacaoService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(avaliacaoMock.getIdAvaliacao(), resultado.getIdAvaliacao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando avaliação não encontrada por ID")
    void deveLancarExcecaoQuandoAvaliacaoNaoEncontradaPorId() {
        // Given
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.buscarPorId(1L));
        
        assertEquals("A avaliação buscada não foi encontrada.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Deve atualizar avaliação com sucesso")
    void deveAtualizarAvaliacaoComSucesso() throws FlopException {
        // Given
        Avaliacao avaliacaoExistente = AvaliacaoMockFactory.criarAvaliacaoHoje();
        avaliacaoExistente.setCriadoEm(LocalDateTime.now()); // Criada hoje
        
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoExistente));
        when(authService.getUsuarioAutenticado()).thenReturn(usuarioMock);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacaoExistente);
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When
        AvaliacaoDTO resultado = avaliacaoService.atualizar(1L, criarAvaliacaoDTO);

        // Then
        assertNotNull(resultado);
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar avaliação de outro usuário")
    void deveLancarExcecaoAoTentarAtualizarAvaliacaoDeOutroUsuario() throws FlopException {
        // Given
        Usuario outroUsuario = UsuarioMockFactory.criarUsuarioPadrao();
        outroUsuario.setIdUsuario(2L);
        
        Avaliacao avaliacaoExistente = AvaliacaoMockFactory.criarAvaliacaoHoje();
        avaliacaoExistente.setUsuario(outroUsuario);
        
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoExistente));
        when(authService.getUsuarioAutenticado()).thenReturn(usuarioMock);
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.atualizar(1L, criarAvaliacaoDTO));
        
        assertEquals("Você não tem permissão para atualizar esta avaliação", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar editar avaliação criada em outro dia")
    void deveLancarExcecaoAoTentarEditarAvaliacaoCriadaEmOutroDia() throws FlopException {
        // Given
        Avaliacao avaliacaoOntem = AvaliacaoMockFactory.criarAvaliacaoOntem();
        
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoOntem));
        when(authService.getUsuarioAutenticado()).thenReturn(usuarioMock);
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.atualizar(1L, criarAvaliacaoDTO));
        
        assertTrue(exception.getMessage().contains("Você só pode editar avaliações criadas no mesmo dia"));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("Deve excluir avaliação como proprietário")
    void deveExcluirAvaliacaoComoProprietario() throws FlopException {
        // Given
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoMock));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // When
        avaliacaoService.excluir(1L, 1L);

        // Then
        verify(avaliacaoRepository).delete(avaliacaoMock);
    }

    @Test
    @DisplayName("Deve excluir avaliação como admin")
    void deveExcluirAvaliacaoComoAdmin() throws FlopException {
        // Given
        Usuario admin = UsuarioMockFactory.criarUsuarioPadrao();
        admin.setIdUsuario(2L);
        admin.setAdmin(true);
        
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoMock));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));

        // When
        avaliacaoService.excluir(1L, 2L);

        // Then
        verify(avaliacaoRepository).delete(avaliacaoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir avaliação sem permissão")
    void deveLancarExcecaoAoTentarExcluirAvaliacaoSemPermissao() {
        // Given
        Usuario outroUsuario = UsuarioMockFactory.criarUsuarioPadrao();
        outroUsuario.setIdUsuario(2L);
        outroUsuario.setAdmin(false);
        
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacaoMock));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(outroUsuario));

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.excluir(1L, 2L));
        
        assertEquals("Você não é o Dono desta avaliação, portanto não pode excluí-la.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("Deve buscar avaliação do usuário hoje na praia com sucesso")
    void deveBuscarAvaliacaoDoUsuarioHojeNaPraiaComSucesso() throws FlopException {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(avaliacaoMock));

        // When
        AvaliacaoDTO resultado = avaliacaoService.buscarAvaliacaoDoUsuarioHojeNaPraia(1L, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(avaliacaoMock.getIdAvaliacao(), resultado.getIdAvaliacao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há avaliação hoje na praia")
    void deveLancarExcecaoQuandoNaoHaAvaliacaoHojeNaPraia() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.buscarAvaliacaoDoUsuarioHojeNaPraia(1L, 1L));
        
        assertTrue(exception.getMessage().contains("Nenhuma avaliação encontrada para hoje na praia"));
        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
    }

    @Test
    @DisplayName("Deve buscar avaliações do usuário hoje")
    void deveBuscarAvaliacoesDoUsuarioHoje() throws FlopException {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndCriadoEmBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(avaliacaoMock));

        // When
        List<AvaliacaoDTO> resultado = avaliacaoService.buscarAvaliacoesDoUsuarioHoje(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve pesquisar com filtros")
    void devePesquisarComFiltros() throws FlopException {
        // Given
        AvaliacaoSeletor seletor = new AvaliacaoSeletor();
        Page<Avaliacao> pageAvaliacoes = new PageImpl<>(List.of(avaliacaoMock));
        
        when(avaliacaoRepository.findAll(eq(seletor), any(Pageable.class)))
                .thenReturn(pageAvaliacoes);

        // When
        Page<AvaliacaoDTO> resultado = avaliacaoService.pesquisarComFiltros(seletor);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve verificar se avaliação existe")
    void deveVerificarSeAvaliacaoExiste() throws FlopException {
        // Given
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(avaliacaoMock));

        // When
        boolean resultado = avaliacaoService.verificarAvaliacaoExistente(1L, 1L);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando avaliação não existe")
    void deveRetornarFalseQuandoAvaliacaoNaoExiste() throws FlopException {
        // Given
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean resultado = avaliacaoService.verificarAvaliacaoExistente(1L, 1L);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar condições incompatíveis - MAR_CALMO com MAR_ONDAS")
    void deveValidarCondicoesIncompatibeisMar() throws FlopException {
        // Given
        criarAvaliacaoDTO.setCondicoes(List.of(Condicoes.MAR_CALMO, Condicoes.MAR_ONDAS));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertTrue(exception.getMessage().contains("As condições selecionadas são incompatíveis"));
    }

    @Test
    @DisplayName("Deve validar condições incompatíveis - LIMPA com LIXO")
    void deveValidarCondicoesIncompatibeisSujeira() throws FlopException {
        // Given
        criarAvaliacaoDTO.setCondicoes(List.of(Condicoes.LIMPA, Condicoes.LIXO));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(praiaRepository.findById(1L)).thenReturn(Optional.of(praiaMock));
        when(avaliacaoRepository.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(
                eq(1L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        doNothing().when(localizacaoService).validarProximidadePraia(anyLong(), anyDouble(), anyDouble());

        // When & Then
        FlopException exception = assertThrows(FlopException.class, 
            () -> avaliacaoService.cadastrar(criarAvaliacaoDTO));
        
        assertTrue(exception.getMessage().contains("As condições selecionadas são incompatíveis"));
    }
}