package com.vitalu.flop.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.auth.TwoFactorAuthUtil;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

	@Autowired
	private AuthService authenticationService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UsuarioService usuarioService;

	@Operation(summary = "Realiza login do usuário", description = "Autentica um usuário e retorna um token de acesso.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
			@ApiResponse(responseCode = "400", description = "Erro de validação nos dados fornecidos") })
	@PostMapping("/login")
	public String login(Authentication authentication) throws FlopException {
		return authenticationService.authenticate(authentication);
	}

	@Operation(summary = "Cadastra um novo usuário", description = "Cadastra um usuário com o perfil de usuário comum.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro de validação") })
	@PostMapping("/novo")
	@ResponseStatus(HttpStatus.CREATED)
	public void cadastrar(@RequestBody Usuario novoUsuario) throws FlopException {
		processarCadastro(novoUsuario, false);
	}

	@Operation(summary = "Cadastra um novo administrador", description = "Cadastra um usuário com o perfil de administrador.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Administrador cadastrado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro de validação") })
	@PostMapping("/novo-admin")
	@ResponseStatus(HttpStatus.CREATED)
	public void cadastrarAdmin(@RequestBody Usuario novoUsuario) throws FlopException {
		processarCadastro(novoUsuario, true);
	}
	
	@PostMapping("/2fa/setup")
	public Map<String, String> setup2FA(@RequestParam String email) throws FlopException {
	    Usuario usuario = (Usuario) usuarioService.loadUserByUsername(email);

	    String secret = TwoFactorAuthUtil.generateSecretKey();
	    usuario.setTwoFactorSecret(secret);
	    usuario.setTwoFactorEnabled(true);

	    usuarioService.atualizar(usuario);

	    return Map.of(
	        "secret", secret,
	        "mensagem", "Adicione essa chave no seu Google Authenticator"
	    );
	}

	private void processarCadastro(Usuario usuario, boolean isAdmin) throws FlopException {
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuario.setAdmin(isAdmin);
		usuarioService.cadastrar(usuario);
	}

}