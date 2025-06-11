package com.vitalu.flop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.LoginRequest;
import com.vitalu.flop.model.dto.LoginResponse;
import com.vitalu.flop.model.dto.TwoFactorVerifyRequest;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.TwoFactorAuthService;
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

	@Autowired
	private TwoFactorAuthService twoFactorAuthService;

	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Método de login padronizado -> Basic Auth
	 * <p>
	 * O parâmetro Authentication já encapsula login (username) e senha (senha)
	 * Basic <Base64 encoded username and senha>
	 *
	 * @param authentication
	 * @return o JWT gerado
	 */

	@Operation(summary = "Realiza login do usuário", description = "Autentica um usuário e retorna um token de acesso.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
			@ApiResponse(responseCode = "400", description = "Erro de validação nos dados fornecidos") })
	@PostMapping("/login")
	public String login(Authentication authentication) throws FlopException {
		return authenticationService.authenticate(authentication);
	}

	// novo endpoint pra login com 2FA
	@Operation(summary = "Login com 2FA", description = "Primeiro passo do login. Se 2FA estiver habilitado, envia código por email.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Credenciais válidas"),
			@ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
			@ApiResponse(responseCode = "400", description = "Erro de validação") })
	@PostMapping("/login-2fa")
	public ResponseEntity<LoginResponse> loginWithTwoFactor(@RequestBody LoginRequest loginRequest) {
		try {
			// Primeiro valida as credenciais básicas
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

			// Se chegou aqui, credenciais estão corretas
			// Gera e envia código 2FA
			twoFactorAuthService.generateAndSendTwoFactorCode(loginRequest.email());

			LoginResponse response = new LoginResponse(true, "Código de verificação enviado para seu email", null);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			LoginResponse response = new LoginResponse(false, "Erro interno do servidor", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/verify-2fa")
	public String verifyTwoFactor(@RequestBody TwoFactorVerifyRequest request) throws FlopException {
		try {

			System.out.println("Email recebido: " + request.email());
			System.out.println("OTP recebido: " + request.otp());

			boolean isValid = twoFactorAuthService.verifyTwoFactorCode(request.email(), request.otp());
			System.out.println("Código válido? " + isValid);

			if (isValid) {
				Usuario usuario = usuarioService.buscarPorEmail(request.email());
				System.out.println("Usuário encontrado: " + (usuario != null ? usuario.getEmail() : "null"));

				String token = authenticationService.generateTokenForUser(usuario);
				System.out.println("Token gerado: " + (token != null ? "SIM" : "NULL"));

				return token;
			} else {
				throw new FlopException("Código de verificação inválido", HttpStatus.UNAUTHORIZED);
			}

		} catch (FlopException e) {
			e.printStackTrace();
			throw e;

		} catch (Exception e) {
			System.out.println("=== ERRO GERAL ===");
			System.out.println("Tipo do erro: " + e.getClass().getSimpleName());
			System.out.println("Mensagem: " + e.getMessage());
			e.printStackTrace();

			throw new FlopException("Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// reenviar código otp
//	@Operation(summary = "Reenvia código 2FA", description = "Reenvia o código de verificação por email.")
//	@PostMapping("/resend-2fa")
//	public ResponseEntity<String> resendTwoFactorCode(@RequestBody String email) {
//		try {
//			twoFactorAuthService.generateAndSendTwoFactorCode(email);
//			return ResponseEntity.ok("Novo código enviado para seu email");
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao reenviar código: " + e.getMessage());
//		}
//	}

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

	private void processarCadastro(Usuario usuario, boolean isAdmin) throws FlopException {
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuario.setAdmin(isAdmin);
		usuarioService.cadastrar(usuario);
	}

}