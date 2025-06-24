package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Denuncia;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.StatusDenuncia;
import com.vitalu.flop.model.mock.DenunciaMockFactory;
import com.vitalu.flop.model.mock.PostagemMockFactory;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.DenunciaRepository;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DenunciaService")
class DenunciaServiceTest {

	@InjectMocks
	private DenunciaService denunciaService;

	@Mock
	private DenunciaRepository denunciaRepository;

	@Mock
	private PostagemRepository postagemRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	private Denuncia denunciaValida;
	private Usuario usuarioValido;
	private Postagem postagemValida;

	@BeforeEach
	void setUp() {
		// Configura mocks básicos que serão usados em vários testes
		denunciaValida = DenunciaMockFactory.criarDenunciaPendente();
		usuarioValido = UsuarioMockFactory.criarUsuarioPadrao();
		postagemValida = PostagemMockFactory.criarPostagemPadrao();
	}

	// Testes para o método cadastrar()
	@Test
	@DisplayName("Deve cadastrar denúncia com sucesso quando os dados são válidos")
	void testCadastrar_ComDadosValidos_DeveRetornarDenunciaSalva() throws FlopException {
		// Arrange
		when(postagemRepository.findById(anyLong())).thenReturn(Optional.of(postagemValida));
		when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioValido));
		when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denunciaValida);

		// Act
		Denuncia denunciaSalva = denunciaService.cadastrar(denunciaValida);

		// Assert
		assertNotNull(denunciaSalva);
		assertEquals(StatusDenuncia.PENDENTE, denunciaSalva.getStatus());
		verify(denunciaRepository, times(1)).save(any(Denuncia.class));
	}

	@Test
	@DisplayName("Deve lançar FlopException ao cadastrar com postagem inexistente")
	void testCadastrar_ComPostagemInexistente_DeveLancarExcecao() {
		// Arrange
		when(postagemRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act & Assert
		FlopException exception = assertThrows(FlopException.class, () -> {
			denunciaService.cadastrar(denunciaValida);
		});

		assertEquals("Denúncia não encontrado.", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
		verify(denunciaRepository, never()).save(any());
	}

	// Testes para o método atualizar()
	@Test
	@DisplayName("Deve atualizar o status da denúncia com sucesso")
	void testAtualizar_ComDadosValidos_DeveAlterarStatus() throws FlopException {
		// Arrange
		when(denunciaRepository.findById(anyLong())).thenReturn(Optional.of(denunciaValida));

		// Act
		denunciaService.atualizar(denunciaValida.getIdDenuncia(), StatusDenuncia.ACEITA);

		// Assert
		ArgumentCaptor<Denuncia> denunciaCaptor = ArgumentCaptor.forClass(Denuncia.class);
		verify(denunciaRepository, times(1)).save(denunciaCaptor.capture());

		Denuncia denunciaAtualizada = denunciaCaptor.getValue();
		assertEquals(StatusDenuncia.ACEITA, denunciaAtualizada.getStatus());
	}

}