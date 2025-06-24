package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.SugestaoDTO;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.mock.SugestaoMockFactory;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.SugestaoRepository;
import com.vitalu.flop.model.seletor.SugestaoSeletor;

public class SugestaoServiceTest {

    @InjectMocks
    private SugestaoService sugestaoService;

    @Mock
    private SugestaoRepository sugestaoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarSugestaoComSucesso() throws FlopException {
        Sugestao novaSugestao = SugestaoMockFactory.criarNovaSugestao();
        Sugestao sugestaoSalva = SugestaoMockFactory.criarSugestaoPadrao();
        
        when(sugestaoRepository.save(novaSugestao)).thenReturn(sugestaoSalva);

        Sugestao resultado = sugestaoService.criarSugestao(novaSugestao);

        assertNotNull(resultado);
        assertEquals(sugestaoSalva.getIdSugestao(), resultado.getIdSugestao());
        verify(sugestaoRepository, times(1)).save(novaSugestao);
    }

    @Test
    void deveLancarErroAoCriarSugestaoComErroNoBanco() {
        Sugestao novaSugestao = SugestaoMockFactory.criarNovaSugestao();
        
        when(sugestaoRepository.save(novaSugestao)).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        assertThrows(RuntimeException.class, () -> sugestaoService.criarSugestao(novaSugestao));
        verify(sugestaoRepository, times(1)).save(novaSugestao);
    }

    @Test
    void deveExcluirSugestaoComSucesso() throws FlopException {
        Usuario usuario = UsuarioMockFactory.criarUsuarioPadrao();
        Sugestao sugestao = SugestaoMockFactory.criarSugestaoComUsuarioEspecifico(usuario);
        
        when(sugestaoRepository.findById(sugestao.getIdSugestao())).thenReturn(Optional.of(sugestao));

        sugestaoService.excluirSugestao(sugestao.getIdSugestao(), usuario.getIdUsuario());

        verify(sugestaoRepository, times(1)).deleteById(sugestao.getIdSugestao());
    }

    @Test
    void deveLancarErroAoExcluirSugestaoInexistente() {
        Long sugestaoId = 99L;
        Long usuarioId = 1L;
        
        when(sugestaoRepository.findById(sugestaoId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> sugestaoService.excluirSugestao(sugestaoId, usuarioId));
        assertEquals("Sugestão não encontrada.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deveLancarErroAoExcluirSugestaoDeOutroUsuario() {
        Usuario proprietario = UsuarioMockFactory.criarUsuarioPadrao();
        Usuario outroUsuario = UsuarioMockFactory.criarUsuarioPadrao();
        outroUsuario.setIdUsuario(2L);
        
        Sugestao sugestao = SugestaoMockFactory.criarSugestaoComUsuarioEspecifico(proprietario);
        
        when(sugestaoRepository.findById(sugestao.getIdSugestao())).thenReturn(Optional.of(sugestao));

        FlopException exception = assertThrows(FlopException.class,
            () -> sugestaoService.excluirSugestao(sugestao.getIdSugestao(), outroUsuario.getIdUsuario()));
        assertEquals("Você não pode excluir sugestões de outras pessoas!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void deveEditarSugestaoComSucesso() throws FlopException {
        Sugestao sugestaoExistente = SugestaoMockFactory.criarSugestaoPadrao();
        Sugestao sugestaoEditada = SugestaoMockFactory.criarSugestaoParaEdicao();
        
        when(sugestaoRepository.findById(sugestaoExistente.getIdSugestao())).thenReturn(Optional.of(sugestaoExistente));
        when(sugestaoRepository.save(any(Sugestao.class))).thenReturn(sugestaoExistente);

        Sugestao resultado = sugestaoService.editarSugestao(sugestaoExistente.getIdSugestao(), sugestaoEditada);

        assertNotNull(resultado);
        verify(sugestaoRepository, times(1)).save(any(Sugestao.class));
    }

    @Test
    void deveLancarErroAoEditarSugestaoInexistente() {
        Long sugestaoId = 99L;
        Sugestao sugestaoEditada = SugestaoMockFactory.criarSugestaoParaEdicao();
        
        when(sugestaoRepository.findById(sugestaoId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> sugestaoService.editarSugestao(sugestaoId, sugestaoEditada));
        assertEquals("Sugestão não encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void devePesquisarTodasAsSugestoes() throws FlopException {
        List<Sugestao> sugestoes = List.of(
            SugestaoMockFactory.criarSugestaoPadrao(),
            SugestaoMockFactory.criarSugestaoAnalisada()
        );
        
        when(sugestaoRepository.findAll()).thenReturn(sugestoes);

        List<SugestaoDTO> resultado = sugestaoService.pesquisarSugestaoTodas();

        assertEquals(2, resultado.size());
        verify(sugestaoRepository, times(1)).findAll();
    }

    @Test
    void deveProcurarSugestaoPorIdComSucesso() throws FlopException {
        Sugestao sugestao = SugestaoMockFactory.criarSugestaoPadrao();
        
        when(sugestaoRepository.findById(sugestao.getIdSugestao())).thenReturn(Optional.of(sugestao));

        SugestaoDTO resultado = sugestaoService.procurarPorId(sugestao.getIdSugestao());

        assertNotNull(resultado);
        assertEquals(sugestao.getNomePraia(), resultado.getNomePraia());
        verify(sugestaoRepository, times(1)).findById(sugestao.getIdSugestao());
    }

    @Test
    void deveLancarErroAoProcurarSugestaoInexistente() {
        Long sugestaoId = 99L;
        
        when(sugestaoRepository.findById(sugestaoId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> sugestaoService.procurarPorId(sugestaoId));
        assertEquals("Esta sugestão não foi encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void devePesquisarSugestoesComFiltros() throws FlopException {
        SugestaoSeletor seletor = new SugestaoSeletor();
        seletor.setPagina(1);
        seletor.setLimite(10);

        List<Sugestao> sugestoes = List.of(SugestaoMockFactory.criarSugestaoPadrao());
        Page<Sugestao> pageSugestoes = new PageImpl<>(sugestoes);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("criadaEm").ascending());
        
        when(sugestaoRepository.findAll(seletor, pageable)).thenReturn(pageSugestoes);

        Page<SugestaoDTO> resultado = sugestaoService.pesquisarSugestaoFiltros(seletor);

        assertEquals(1, resultado.getTotalElements());
        verify(sugestaoRepository, times(1)).findAll(seletor, pageable);
    }

    @Test
    void devePesquisarSugestoesSemPaginacao() throws FlopException {
        SugestaoSeletor seletor = new SugestaoSeletor();
        // Não definir página e limite para testar Pageable.unpaged()

        List<Sugestao> sugestoes = List.of(SugestaoMockFactory.criarSugestaoPadrao());
        Page<Sugestao> pageSugestoes = new PageImpl<>(sugestoes);
        
        when(sugestaoRepository.findAll(seletor, Pageable.unpaged())).thenReturn(pageSugestoes);

        Page<SugestaoDTO> resultado = sugestaoService.pesquisarSugestaoFiltros(seletor);

        assertEquals(1, resultado.getTotalElements());
        verify(sugestaoRepository, times(1)).findAll(seletor, Pageable.unpaged());
    }

    @Test
    void deveAlternarStatusAnaliseParaNaoAnalisada() throws FlopException {
        Sugestao sugestao = SugestaoMockFactory.criarSugestaoAnalisada(); // Começa como analisada (true)
        
        when(sugestaoRepository.findByIdSugestao(sugestao.getIdSugestao())).thenReturn(Optional.of(sugestao));
        when(sugestaoRepository.save(any(Sugestao.class))).thenReturn(sugestao);

        Sugestao resultado = sugestaoService.alternarStatusAnalise(sugestao.getIdSugestao());

        assertFalse(resultado.getAnalisada()); // Deve ter mudado para false
        verify(sugestaoRepository, times(1)).save(any(Sugestao.class));
    }

    @Test
    void deveAlternarStatusAnaliseParaAnalisada() throws FlopException {
        Sugestao sugestao = SugestaoMockFactory.criarSugestaoPadrao(); // Começa como não analisada (false)
        
        when(sugestaoRepository.findByIdSugestao(sugestao.getIdSugestao())).thenReturn(Optional.of(sugestao));
        when(sugestaoRepository.save(any(Sugestao.class))).thenReturn(sugestao);

        Sugestao resultado = sugestaoService.alternarStatusAnalise(sugestao.getIdSugestao());

        assertTrue(resultado.getAnalisada()); // Deve ter mudado para true
        verify(sugestaoRepository, times(1)).save(any(Sugestao.class));
    }

    @Test
    void deveLancarErroAoAlternarStatusAnaliseInexistente() {
        Long sugestaoId = 99L;
        
        when(sugestaoRepository.findByIdSugestao(sugestaoId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> sugestaoService.alternarStatusAnalise(sugestaoId));
        assertEquals("Esta sugestão não foi encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}