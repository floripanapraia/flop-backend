package com.vitalu.flop.controller;

import java.util.List;

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
import com.vitalu.flop.model.dto.SugestaoDTO;
import com.vitalu.flop.model.entity.Sugestao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.seletor.SugestaoSeletor;
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
		if (subject.isAdmin() == false) {
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

	@Operation(summary = "Listar todas as sugestões.", description = "Retorna uma lista de todas as sugestões cadastrados no sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista de sugestões retornada com sucesso") })
	@GetMapping(path = "/todos")
	public List<SugestaoDTO> pesquisarSugestaoTodas() throws FlopException {
		return sugestaoService.pesquisarSugestaoTodas();
	}

	@Operation(summary = "Buscar sugestão por ID")
	@GetMapping("/{idSugestao}")
	public ResponseEntity<SugestaoDTO> procurarPorId(@PathVariable Long sugestaoId) throws FlopException {
		SugestaoDTO sugestao = sugestaoService.procurarPorId(sugestaoId);
		return ResponseEntity.ok(sugestao);
	}

	@Operation(summary = "Pesquisar com filtro", description = "Retorna uma lista de sugestões de acordo com o filtro selecionado.", responses = {
			@ApiResponse(responseCode = "200", description = "Sugestões filtradas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Sugestao.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(description = "Detalhes do erro interno", example = "{\"message\": \"Erro interno do servidor\", \"status\": 500}"))) })
	@PostMapping("/filtrar")
	public ResponseEntity<Page<SugestaoDTO>> pesquisarSugestaoFiltros(@RequestBody SugestaoSeletor seletor) throws FlopException {
		Page<SugestaoDTO> resultado = sugestaoService.pesquisarSugestaoFiltros(seletor);
		return ResponseEntity.ok(resultado);
	}
	
}
