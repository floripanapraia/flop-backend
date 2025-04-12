package com.vitalu.flop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.PraiaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/praia")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class PraiaController {

	@Autowired
	private PraiaService praiaService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Inserir nova praia.", responses = {
			@ApiResponse(responseCode = "200", description = "Praia criada com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Praia.class))),
			@ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio.", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}"))) })
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<Praia> cadastrarPraia(@Valid @RequestBody PraiaDTO novaPraia) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (subject.isAdmin() == true) {
			Praia praiaCriada = praiaService.cadastrarPraia(novaPraia);
			return ResponseEntity.status(201).body(praiaCriada);
		} else {
			throw new FlopException("Usuários não podem criar novas praias.", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Listar todas as praias.", description = "Retorna uma lista de todas as praias cadastrados no sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista de praias retornada com sucesso") })
	@GetMapping(path = "/todos")
	public List<Praia> pesquisarTodos() throws FlopException {
		return praiaService.pesquisarPraiaTodas();
	}

	@Operation(summary = "Pesquisar praia por ID.", description = "Busca uma praia específica através do seu ID.")
	@GetMapping(path = "/{idPraia}")
	public ResponseEntity<Praia> pesquisarPraiasId(@PathVariable("idPraia") Long praiaId) throws FlopException {
		Praia praia = praiaService.pesquisarPraiasId(praiaId);
		return ResponseEntity.ok(praia);
	}
}
