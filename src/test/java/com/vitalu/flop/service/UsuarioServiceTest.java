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

}
