package com.vitalu.flop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Localizacao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.LocalizacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/localizacao")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class LocalizacaoController {

	@Autowired
	private LocalizacaoService localizacaoService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Inserir nova localização", responses = {
			@ApiResponse(responseCode = "200", description = "Localização criada com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localizacao.class))),
			@ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio.", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}"))) })
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<Localizacao> criarLocalizacao(@Valid @RequestBody Localizacao novoLocal) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (subject.isAdmin() == true) {
			Localizacao localizacaoCriada = localizacaoService.criarLocalizacao(novoLocal);
			return ResponseEntity.status(201).body(localizacaoCriada);
		} else {
			throw new FlopException("Apenas administradores podem criar novas localizações.", HttpStatus.BAD_REQUEST);
		}
	}
	
	@Operation(summary = "Excluir localização")
	@ApiResponse(responseCode = "201", description = "Localização excluída com sucesso!")
	@DeleteMapping("/excluir/{localizacaoId}")
	public ResponseEntity<Void> excluirLocalizacao(@PathVariable Long localizacaoId) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		if (subject.isAdmin() == true) {
			localizacaoService.excluirLocalizacao(localizacaoId);
			return ResponseEntity.noContent().build();
		} else {
			throw new FlopException("Apenas administradores podem excluir localizações.", HttpStatus.BAD_REQUEST);
		}
	}
}
