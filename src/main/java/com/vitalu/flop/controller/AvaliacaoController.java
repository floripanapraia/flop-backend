package com.vitalu.flop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.service.AvaliacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/avaliacoes")
@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 3600)
public class AvaliacaoController {

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Cadastrar avaliação", description = "Cadastra uma nova avaliação de praia.")
	@ApiResponse(responseCode = "201", description = "Avaliação cadastrada com sucesso")
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<Avaliacao> cadastrar(@Valid @RequestBody Avaliacao novaAvaliacao) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (subject.isAdmin() == false) {
			novaAvaliacao.setUsuario(subject);
			Avaliacao avaliacaoCriada = avaliacaoService.cadastrar(novaAvaliacao);
			return ResponseEntity.status(201).body(avaliacaoCriada);
		} else {
			throw new FlopException("Administradores não podem fazer avaliações.", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Atualizar avaliação", description = "Atualiza as condições de uma avaliação existente.")
	@PutMapping(path = "/atualizar/{idAvaliacao}")
	public ResponseEntity<Avaliacao> atualizar(@PathVariable Long idAvaliacao, @RequestBody Avaliacao editarAvaliacao)
			throws FlopException {

		Avaliacao atualizada = avaliacaoService.atualizar(idAvaliacao, editarAvaliacao);
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
	public ResponseEntity<Avaliacao> buscarPorId(@PathVariable Long idAvaliacao) throws FlopException {
		Avaliacao avaliacao = avaliacaoService.buscarPorId(idAvaliacao);
		return ResponseEntity.ok(avaliacao);
	}

	// SELETOR
//	@Operation(summary = "Listar avaliações por praia", description = "Retorna uma lista de avaliações para uma praia específica.")
//	@GetMapping("/por-praia/{idPraia}")
//	public ResponseEntity<List<Avaliacao>> listarAvaliacoesPorPraia(@PathVariable Long idPraia) {
//		Praia praia = new Praia();
//		praia.setIdPraia(idPraia);
//		List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorPraia(praia);
//		return ResponseEntity.ok(avaliacoes);
//	}
//
//	@Operation(summary = "Listar avaliações por usuário", description = "Retorna uma lista de avaliações feitas por um usuário específico.")
//	@GetMapping("/por-usuario/{idUsuario}")
//	public ResponseEntity<List<Avaliacao>> listarAvaliacoesPorUsuario(@PathVariable Long idUsuario) {
//		Usuario usuario = new Usuario();
//		usuario.setIdUsuario(idUsuario);
//		List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorUsuario(usuario);
//		return ResponseEntity.ok(avaliacoes);
//	}
}
