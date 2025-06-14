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
import com.vitalu.flop.model.dto.AvaliacaoDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.seletor.AvaliacaoSeletor;
import com.vitalu.flop.service.AvaliacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/avaliacoes")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class AvaliacaoController {

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Cadastrar avaliação", description = "Cadastra uma nova avaliação de praia.")
	@ApiResponse(responseCode = "201", description = "Avaliação cadastrada com sucesso")
	@ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário não autorizado")
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<AvaliacaoDTO> cadastrar(@Valid @RequestBody AvaliacaoDTO novaAvaliacaoDTO)
			throws FlopException {
		if (novaAvaliacaoDTO == null) {
			throw new FlopException("Dados da avaliação inválidos.", HttpStatus.BAD_REQUEST);
		}

		Usuario usuarioAutenticado = authService.getUsuarioAutenticado();
		if (usuarioAutenticado.isAdmin()) {
			throw new FlopException("Administradores não podem fazer avaliações.", HttpStatus.BAD_REQUEST);
		}

		novaAvaliacaoDTO.setIdUsuario(usuarioAutenticado.getIdUsuario());

		AvaliacaoDTO avaliacaoCriada = avaliacaoService.cadastrar(novaAvaliacaoDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoCriada);
	}

	@Operation(summary = "Atualizar avaliação", description = "Atualiza as condições de uma avaliação existente.")
	@PutMapping(path = "/atualizar/{idAvaliacao}")
	public ResponseEntity<AvaliacaoDTO> atualizar(@PathVariable Long idAvaliacao,
			@RequestBody AvaliacaoDTO editarAvaliacao) throws FlopException {

		AvaliacaoDTO atualizada = avaliacaoService.atualizar(idAvaliacao, editarAvaliacao);
		return ResponseEntity.status(200).body(atualizada);
	}

	@Operation(summary = "Excluir avaliação", description = "Exclui a avaliação", responses = {
			@ApiResponse(responseCode = "200", description = "Avaliação excluída com sucesso"),
			@ApiResponse(responseCode = "404", description = "Avaliação não encontrada") })
	@DeleteMapping("/excluir/{idAvaliacao}")
	public ResponseEntity<Void> excluir(@PathVariable Long idAvaliacao) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		avaliacaoService.excluir(idAvaliacao, subject.getIdUsuario());
		return ResponseEntity.noContent().build();

	}

	@Operation(summary = "Buscar avaliação por ID", description = "Busca uma avaliação específica através do seu ID.")
	@GetMapping("/{idAvaliacao}")
	public ResponseEntity<AvaliacaoDTO> buscarPorId(@PathVariable Long idAvaliacao) throws FlopException {
		AvaliacaoDTO avaliacao = avaliacaoService.buscarPorId(idAvaliacao);
		return ResponseEntity.ok(avaliacao);
	}

	@Operation(summary = "Buscar avaliação do usuário hoje na praia", description = "Busca a avaliação feita pelo usuário hoje em uma praia específica.")
	@GetMapping("/usuario/{idUsuario}/praia/{idPraia}/hoje")
	public ResponseEntity<AvaliacaoDTO> buscarAvaliacaoUsuarioHojeNaPraia(@PathVariable Long idUsuario,
			@PathVariable Long idPraia) throws FlopException {

		AvaliacaoDTO avaliacao = avaliacaoService.buscarAvaliacaoDoUsuarioHojeNaPraia(idUsuario, idPraia);
		return ResponseEntity.ok(avaliacao);
	}
	
	@Operation(summary = "Verificar avaliação existente", description = "Verifica se o usuário já fez uma avaliação hoje na praia específica.")
	@GetMapping("/usuario/{idUsuario}/praia/{idPraia}/existe")
	public ResponseEntity<Boolean> verificarAvaliacaoExistente(@PathVariable Long idUsuario, @PathVariable Long idPraia) {
	    try {
	        boolean existe = avaliacaoService.verificarAvaliacaoExistente(idUsuario, idPraia);
	        return ResponseEntity.ok(existe);
	    } catch (FlopException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);  // Em caso de erro, retorna false
	    }
	}

	@Operation(summary = "Pesquisar com filtro", description = "Retorna uma lista de avaliacoes de acordo com o filtro selecionado.", responses = {
			@ApiResponse(responseCode = "200", description = "Avaliações filtradas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Avaliacao.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(description = "Detalhes do erro interno", example = "{\"message\": \"Erro interno do servidor\", \"status\": 500}"))) })
	@PostMapping("/filtrar")
	public ResponseEntity<Page<AvaliacaoDTO>> pesquisarComFiltros(@RequestBody AvaliacaoSeletor seletor)
			throws FlopException {
		Page<AvaliacaoDTO> resultado = avaliacaoService.pesquisarComFiltros(seletor);
		return ResponseEntity.ok(resultado);
	}
}