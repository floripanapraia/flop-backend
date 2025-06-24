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
}