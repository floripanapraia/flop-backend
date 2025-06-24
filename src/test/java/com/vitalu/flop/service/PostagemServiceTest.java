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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.PostagemDTO;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.mock.PostagemMockFactory;
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

	private Postagem postagemValida;
	private Usuario usuarioDono;
	private Usuario usuarioAdmin;

	@BeforeEach
	void setUp() {
		postagemValida = PostagemMockFactory.criarPostagemPadrao();
		usuarioDono = UsuarioMockFactory.criarUsuarioPadrao(); // ID 1L, admin=false
		usuarioAdmin = UsuarioMockFactory.criarUsuarioAdmin(); // ID 1L, mas vamos mudar para 2L para diferenciar
		usuarioAdmin.setIdUsuario(2L);
	}
}