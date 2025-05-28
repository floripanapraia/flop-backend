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
import com.vitalu.flop.model.dto.PraiaDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.seletor.PraiaSeletor;
import com.vitalu.flop.service.PraiaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/praias")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class PraiaController {

	@Autowired
	private PraiaService praiaService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Inserir nova praia.", responses = {
			@ApiResponse(responseCode = "200", description = "Praia criada com sucesso!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Praia.class))),
			@ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio.", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}"))),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado")})
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

	@Operation(summary = "Excluir praia")
	@ApiResponse(responseCode = "204", description = "Praia excluída com sucesso")
	@ApiResponse(responseCode = "403", description = "Apenas administradores podem excluir praias")
	@ApiResponse(responseCode = "404", description = "Praia não encontrada")
	@ApiResponse(responseCode = "401", description = "Usuário não autenticado")
	@DeleteMapping("/excluir/{praiaId}")
	public ResponseEntity<Void> excluirPraia(@PathVariable Long praiaId) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (!subject.isAdmin()) {
			throw new FlopException("Apenas administradores podem excluir praias", HttpStatus.FORBIDDEN);
		}

		praiaService.excluirPraia(praiaId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Excluir praia")
	@ApiResponse(responseCode = "204", description = "Praia editada com sucesso")
	@ApiResponse(responseCode = "403", description = "Apenas administradores podem editar praias")
	@ApiResponse(responseCode = "404", description = "Praia não encontrada")
	@ApiResponse(responseCode = "401", description = "Usuário não autenticado")
	@PutMapping("/editar/{praiaId}")
	public ResponseEntity<Praia> editarPraia(@PathVariable Long praiaId, @RequestBody PraiaDTO praiaEditadaDto)
			throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (subject.isAdmin() == true) {
			Praia praiaAtualizada = praiaService.editarPraia(praiaEditadaDto, praiaId);
			return ResponseEntity.ok(praiaAtualizada);
		} else {
			throw new FlopException("Apenas administradores podem editar praias!", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@Operation(summary = "Listar todas as praias.", description = "Retorna uma lista de todas as praias cadastrados no sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista de praias retornada com sucesso") })
	@GetMapping(path = "/todos")
	public List<PraiaDTO> pesquisarTodos() throws FlopException {
		return praiaService.pesquisarPraiaTodas();
	}

	@Operation(summary = "Pesquisar praia por ID.", description = "Busca uma praia específica através do seu ID.")
	@GetMapping(path = "/{idPraia}")
	public ResponseEntity<PraiaDTO> pesquisarPraiasId(@PathVariable("idPraia") Long idPraia) throws FlopException {
		PraiaDTO praia = praiaService.pesquisarPraiasId(idPraia);
		return ResponseEntity.ok(praia);
	}

	@Operation(summary = "Pesquisar com filtro", description = "Retorna uma lista de praias de acordo com o filtro selecionado.", responses = {
			@ApiResponse(responseCode = "200", description = "Praias filtradas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Praia.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(description = "Detalhes do erro interno", example = "{\"message\": \"Erro interno do servidor\", \"status\": 500}"))) })
	@PostMapping("/filtrar")
	public ResponseEntity<Page<PraiaDTO>> pesquisarPraiaFiltros(@RequestBody PraiaSeletor seletor)
			throws FlopException {
		Page<PraiaDTO> resultado = praiaService.pesquisarPraiaFiltros(seletor);
		return ResponseEntity.ok(resultado);
	}

	@Operation(summary = "Pesquisa as avaliações do dia", description = "Retorna as avaliações do dia.")
	@GetMapping("/{praiaId}/avaliacoes")
	public List<Avaliacao> avaliacoesDoDia(@PathVariable Long praiaId) {
		return praiaService.buscarAvaliacoesDoDia(praiaId);
	}

	@Operation(summary = "Pesquisa as avaliações do dia", description = "Retorna as avaliações do dia.")
	@GetMapping("/{praiaId}/postagens")
	public ResponseEntity<List<Postagem>> postagensDoDia(@PathVariable Long praiaId) {
		List<Postagem> postagensDoDia = praiaService.buscarPostagensDoDia(praiaId);
		return ResponseEntity.ok(postagensDoDia);
	}

	@Operation(summary = "Retorna as informações da praia atualizadas.", description = "Apresenta as postagens, avaliações e imagens do dia.")
	@GetMapping("/{praiaId}/hoje")
	public ResponseEntity<PraiaDTO> informacoesPraiaHoje(@PathVariable Long praiaId) throws FlopException {
		PraiaDTO praiaHoje = praiaService.obterInformacoesPraiaHoje(praiaId);
		return ResponseEntity.ok(praiaHoje);
	}

}
