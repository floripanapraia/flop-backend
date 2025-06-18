package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.mock.PraiaMockFactory;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.PraiaSeletor;

public class PraiaServiceTest {

    @InjectMocks
    private PraiaService praiaService;

    @Mock
    private PraiaRepository praiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private PostagemRepository postagemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarPraiaComSucesso() throws FlopException {
        PraiaDTO dto = new PraiaDTO();
        dto.setNomePraia("Praia Nova");
        dto.setLatitude(-22.9711);
        dto.setLongitude(-43.1822);
        dto.setPlaceId("ChIJSamplePlaceId");
        dto.setImagem("https://exemplo.com/praia.jpg");

        Praia praiaSalva = PraiaMockFactory.criarPraiaPadrao();
        when(praiaRepository.save(any(Praia.class))).thenReturn(praiaSalva);

        Praia resultado = praiaService.cadastrarPraia(dto);

        assertNotNull(resultado);
        assertEquals(praiaSalva.getIdPraia(), resultado.getIdPraia());
        verify(praiaRepository, times(1)).save(any(Praia.class));
    }

    @Test
    void deveExcluirPraiaComSucesso() throws FlopException {
        Praia praia = PraiaMockFactory.criarPraiaPadrao();
        when(praiaRepository.findById(praia.getIdPraia())).thenReturn(Optional.of(praia));

        praiaService.excluirPraia(praia.getIdPraia());

        verify(praiaRepository, times(1)).deleteById(praia.getIdPraia());
    }

    @Test
    void deveLancarErroAoExcluirPraiaInexistente() {
        Long praiaId = 99L;
        when(praiaRepository.findById(praiaId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class, 
            () -> praiaService.excluirPraia(praiaId));
        assertEquals("Praia não encontrada", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void devePesquisarTodasAsPraias() throws FlopException {
        List<Praia> praias = List.of(
            PraiaMockFactory.criarPraiaPadrao(),
            PraiaMockFactory.criarPraiaIpanema()
        );

        when(praiaRepository.findAll()).thenReturn(praias);
        when(avaliacaoRepository.findByPraia_IdPraia(anyLong())).thenReturn(new ArrayList<>());
        when(postagemRepository.findByPraia_IdPraia(anyLong())).thenReturn(new ArrayList<>());

        List<PraiaDTO> resultado = praiaService.pesquisarPraiaTodas();

        assertEquals(2, resultado.size());
        verify(praiaRepository, times(1)).findAll();
    }

    @Test
    void devePesquisarPraiaPorIdComSucesso() throws FlopException {
        Praia praia = PraiaMockFactory.criarPraiaPadrao();
        when(praiaRepository.findById(praia.getIdPraia())).thenReturn(Optional.of(praia));
        when(avaliacaoRepository.findByPraia_IdPraia(praia.getIdPraia())).thenReturn(new ArrayList<>());
        when(postagemRepository.findByPraia_IdPraia(praia.getIdPraia())).thenReturn(new ArrayList<>());

        PraiaDTO resultado = praiaService.pesquisarPraiasId(praia.getIdPraia());

        assertNotNull(resultado);
        assertEquals(praia.getNomePraia(), resultado.getNomePraia());
        verify(praiaRepository, times(1)).findById(praia.getIdPraia());
    }

    @Test
    void deveLancarErroAoPesquisarPraiaInexistente() {
        Long praiaId = 99L;
        when(praiaRepository.findById(praiaId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> praiaService.pesquisarPraiasId(praiaId));
        assertEquals("Esta praia não foi encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void devePesquisarPraiasComFiltros() throws FlopException {
        PraiaSeletor seletor = new PraiaSeletor();
        seletor.setPagina(1);
        seletor.setLimite(10);

        List<Praia> praias = List.of(PraiaMockFactory.criarPraiaPadrao());
        Page<Praia> pagePraias = new PageImpl<>(praias);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nomePraia").ascending());
        
        when(praiaRepository.findAll(seletor, pageable)).thenReturn(pagePraias);
        when(avaliacaoRepository.findByPraia_IdPraia(anyLong())).thenReturn(new ArrayList<>());
        when(postagemRepository.findByPraia_IdPraia(anyLong())).thenReturn(new ArrayList<>());

        Page<PraiaDTO> resultado = praiaService.pesquisarPraiaFiltros(seletor);

        assertEquals(1, resultado.getTotalElements());
        verify(praiaRepository, times(1)).findAll(seletor, pageable);
    }

    @Test
    void deveExcluirPraiaComoAdminComSucesso() throws FlopException {
        Praia praia = PraiaMockFactory.criarPraiaPadrao();
        Usuario admin = UsuarioMockFactory.criarUsuarioAdmin();

        when(praiaRepository.findById(praia.getIdPraia())).thenReturn(Optional.of(praia));
        when(usuarioRepository.findById(admin.getIdUsuario())).thenReturn(Optional.of(admin));

        praiaService.excluirPraia(praia.getIdPraia(), admin.getIdUsuario());

        verify(praiaRepository, times(1)).deleteById(praia.getIdPraia());
    }

    @Test
    void deveLancarErroAoExcluirPraiaSemSerAdmin() {
        Praia praia = PraiaMockFactory.criarPraiaPadrao();
        Usuario usuarioComum = UsuarioMockFactory.criarUsuarioPadrao();

        when(praiaRepository.findById(praia.getIdPraia())).thenReturn(Optional.of(praia));
        when(usuarioRepository.findById(usuarioComum.getIdUsuario())).thenReturn(Optional.of(usuarioComum));

        FlopException exception = assertThrows(FlopException.class,
            () -> praiaService.excluirPraia(praia.getIdPraia(), usuarioComum.getIdUsuario()));
        assertEquals("Apenas administradores podem excluir praias!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void deveEditarPraiaComSucesso() throws FlopException {
        Praia praiaExistente = PraiaMockFactory.criarPraiaPadrao();
        
        PraiaDTO praiaEditada = new PraiaDTO();
        praiaEditada.setNomePraia("Praia Editada");
        praiaEditada.setLatitude(-23.0000);
        praiaEditada.setLongitude(-44.0000);
        praiaEditada.setPlaceId("NovoPlaceId");
        praiaEditada.setImagem("https://exemplo.com/nova-imagem.jpg");

        when(praiaRepository.findById(praiaExistente.getIdPraia())).thenReturn(Optional.of(praiaExistente));
        when(praiaRepository.save(any(Praia.class))).thenReturn(praiaExistente);

        Praia resultado = praiaService.editarPraia(praiaEditada, praiaExistente.getIdPraia());

        assertNotNull(resultado);
        verify(praiaRepository, times(1)).save(any(Praia.class));
    }

    @Test
    void deveLancarErroAoEditarPraiaInexistente() {
        Long praiaId = 99L;
        PraiaDTO praiaEditada = new PraiaDTO();
        praiaEditada.setNomePraia("Praia Editada");

        when(praiaRepository.findById(praiaId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> praiaService.editarPraia(praiaEditada, praiaId));
        assertEquals("Praia não localizada.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deveBuscarAvaliacoesDoDia() {
        Long praiaId = 1L;
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX);

        List<Avaliacao> avaliacoesMock = new ArrayList<>();
        when(avaliacaoRepository.findAvaliacoesDoDia(praiaId, inicioDoDia, fimDoDia))
            .thenReturn(avaliacoesMock);

        List<Avaliacao> resultado = praiaService.buscarAvaliacoesDoDia(praiaId);

        assertEquals(avaliacoesMock, resultado);
        verify(avaliacaoRepository, times(1)).findAvaliacoesDoDia(praiaId, inicioDoDia, fimDoDia);
    }

    @Test
    void deveObterInformacoesPraiaHoje() throws FlopException {
        Praia praia = PraiaMockFactory.criarPraiaPadrao();
        List<Avaliacao> avaliacoesDoDia = new ArrayList<>();

        when(praiaRepository.findById(praia.getIdPraia())).thenReturn(Optional.of(praia));
        when(avaliacaoRepository.findAvaliacoesDoDia(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(avaliacoesDoDia);

        PraiaDTO resultado = praiaService.obterInformacoesPraiaHoje(praia.getIdPraia());

        assertNotNull(resultado);
        assertEquals(praia.getNomePraia(), resultado.getNomePraia());
        assertEquals(0, resultado.getTotalAvaliacoesDoDia());
    }

    @Test
    void deveLancarErroAoObterInformacoesPraiaInexistente() {
        Long praiaId = 99L;
        when(praiaRepository.findById(praiaId)).thenReturn(Optional.empty());

        FlopException exception = assertThrows(FlopException.class,
            () -> praiaService.obterInformacoesPraiaHoje(praiaId));
        assertEquals("Praia não localizada.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}