package com.vitalu.flop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.SugestaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/sugestoes")
@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 3600)
public class SugestaoController {

	@Autowired
	private SugestaoService sugestaoService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Inserir nova sugestão", responses = {
			@ApiResponse(responseCode = "200", description = "Sugestão criada com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sugestao.class))),
			@ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio.", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}"))) })
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<Sugestao> criarSugestao(@Valid @RequestBody Sugestao novaSugestao) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (subject.isAdmin() == true) {
			novaSugestao.setUsuario(subject);
			Sugestao sugestaoCriada = sugestaoService.criarSugestao(novaSugestao);
			return ResponseEntity.status(201).body(sugestaoCriada);
		} else {
			throw new FlopException("Administradores não podem criar sugestões.", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Excluir sugestão")
	@ApiResponse(responseCode = "201", description = "Sugestão excluída com sucesso!")
	@DeleteMapping("/excluir/{sugestaoId}")
	public ResponseEntity<Void> excluirSugestao(@PathVariable Long sugestaoId) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		sugestaoService.excluirSugestao(sugestaoId, subject.getIdUsuario());

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Editar sugestão")
	@ApiResponse(responseCode = "201", description = "Sugestão editada com sucesso!")
	@PutMapping(path = "/editar/{idSugestao}")
	public ResponseEntity<Sugestao> editarSugestao(@PathVariable Long sugestaoId, @RequestBody Sugestao editarSugestao)
			throws FlopException {

		Sugestao atualizada = sugestaoService.editarSugestao(sugestaoId, editarSugestao);
		return ResponseEntity.status(201).body(atualizada);
	}

	@Operation(summary = "Buscar sugestão por ID")
	@GetMapping("/{idSugestao}")
	public ResponseEntity<Sugestao> procurarPorId(@PathVariable Long sugestaoId) throws FlopException {
		Sugestao sugestao = sugestaoService.procurarPorId(sugestaoId);
		return ResponseEntity.ok(sugestao);
	}

	public Page<Sugestao> pesquisarSugestaoFiltros(Long sugestaoId) throws FlopException {
		// TODO
		return null;
	}
}
