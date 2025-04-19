package com.vitalu.flop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.UsuarioService;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

	@Autowired
	private AuthService authenticationService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UsuarioService usuarioService;

	/**
	 * Método de login padronizado -> Basic Auth
	 * <p>
	 * O parâmetro Authentication já encapsula login (username) e senha (senha)
	 * Basic <Base64 encoded username and senha>
	 *
	 * @param authentication
	 * @return o JWT gerado
	 */

	@PostMapping("/login")
	public String login(Authentication authentication) throws FlopException {
		return authenticationService.authenticate(authentication);
	}

	@PostMapping("/novo")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void cadastrar(@RequestBody Usuario novoUsuario) throws FlopException {
		String senhaCifrada = passwordEncoder.encode(novoUsuario.getSenha());
		novoUsuario.setSenha(senhaCifrada);
		usuarioService.cadastrar(novoUsuario);
	}

	@PostMapping("/novo-admin")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void cadastrarAdmin(@RequestBody Usuario novoUsuario) throws FlopException {
		String senhaCifrada = passwordEncoder.encode(novoUsuario.getSenha());
		novoUsuario.setSenha(senhaCifrada);
		novoUsuario.setAdmin(true);

		usuarioService.cadastrarAdmin(novoUsuario);
	}

}