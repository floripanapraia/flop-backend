package com.vitalu.flop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.mock.UsuarioMockFactory;
import com.vitalu.flop.model.repository.UsuarioRepository;

public class UsuarioServiceTest {

	@InjectMocks
	private UsuarioService usuarioService;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private AuthService authService;

	@Mock
	private PasswordEncoder encoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deveCarregarUsuarioPorEmailComSucesso() {
		Usuario usuario = UsuarioMockFactory.criarUsuarioPadrao();
		when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

		UserDetails userDetails = usuarioService.loadUserByUsername(usuario.getEmail());

		assertEquals(usuario.getEmail(), userDetails.getUsername());
		verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
	}

	@Test
	void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
		String email = "inexistente@email.com";
		when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername(email));
	}

	@Test
	void deveLancarExcecaoQuandoUsuarioEstaBloqueado() {
		Usuario bloqueado = UsuarioMockFactory.criarUsuarioBloqueado();
		when(usuarioRepository.findByEmail(bloqueado.getEmail())).thenReturn(Optional.of(bloqueado));

		assertThrows(DisabledException.class, () -> usuarioService.loadUserByUsername(bloqueado.getEmail()));
	}

	@Test
	void deveCadastrarUsuarioComSucesso() throws FlopException {
		Usuario novoUsuario = UsuarioMockFactory.criarUsuarioPadrao();
		when(usuarioRepository.existsByEmailIgnoreCase(novoUsuario.getEmail())).thenReturn(false);
		when(usuarioRepository.existsByNickname(novoUsuario.getUsername())).thenReturn(false);

		usuarioService.cadastrar(novoUsuario);

		verify(usuarioRepository).save(novoUsuario);
	}

	@Test
	void deveLancarErroAoCadastrarComEmailDuplicado() {
		Usuario novoUsuario = UsuarioMockFactory.criarUsuarioPadrao();
		when(usuarioRepository.existsByEmailIgnoreCase(novoUsuario.getEmail())).thenReturn(true);

		FlopException exception = assertThrows(FlopException.class, () -> usuarioService.cadastrar(novoUsuario));
		assertEquals("O e-mail informado já está cadastrado.", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	@Test
	void deveAtualizarUsuarioComSucesso() throws FlopException {
		Usuario usuario = UsuarioMockFactory.criarUsuarioPadrao();
		usuario.setSenha("novaSenha");
		when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
		when(encoder.encode("novaSenha")).thenReturn("senhaCriptografada");
		when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

		Usuario atualizado = usuarioService.atualizar(usuario);

		assertEquals("senhaCriptografada", atualizado.getSenha());
	}

	// TODO: rever após mocks de postagem
//	@Test
//	void deveExcluirUsuarioComSucesso() throws FlopException {
//		Usuario usuario = UsuarioMockFactory.criarUsuarioPadrao();
//		when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
//
//		usuarioService.excluir(usuario.getIdUsuario());
//
//		verify(usuarioRepository).deleteById(usuario.getIdUsuario());
//	}

	@Test
	void deveLancarErroAoExcluirUsuarioInexistente() {
		when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

		FlopException exception = assertThrows(FlopException.class, () -> usuarioService.excluir(99L));
		assertEquals("Usuário não encontrado.", exception.getMessage());
	}

	@Test
	void deveBloquearOutroUsuarioComSucesso() throws FlopException {
		Usuario usuario = UsuarioMockFactory.criarUsuarioPadrao();
		Usuario admin = UsuarioMockFactory.criarUsuarioAdmin();
		admin.setIdUsuario(999L);

		when(usuarioRepository.findById(usuario.getIdUsuario())).thenReturn(Optional.of(usuario));
		when(authService.getUsuarioAutenticado()).thenReturn(admin);
		when(usuarioRepository.save(any())).thenReturn(usuario);

		Usuario bloqueado = usuarioService.bloquearUsuario(usuario.getIdUsuario(), true);

		assertTrue(bloqueado.isBloqueado());
	}

	@Test
	void deveLancarErroAoBloquearProprioUsuario() throws FlopException {
		Usuario admin = UsuarioMockFactory.criarUsuarioAdmin();
		when(usuarioRepository.findById(admin.getIdUsuario())).thenReturn(Optional.of(admin));
		when(authService.getUsuarioAutenticado()).thenReturn(admin);

		FlopException exception = assertThrows(FlopException.class,
				() -> usuarioService.bloquearUsuario(admin.getIdUsuario(), true));
		assertEquals("Não é possível bloquear sua própria conta.", exception.getMessage());
	}

}
