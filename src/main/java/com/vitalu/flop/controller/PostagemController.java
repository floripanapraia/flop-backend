package com.vitalu.flop.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.PostagemDTO;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.seletor.PostagemSeletor;
import com.vitalu.flop.service.PostagemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/postagens")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class PostagemController {

	@Autowired
	private PostagemService postagemService;
	@Autowired
	private AuthService authService;

	@PostMapping("/salvar-foto")
	public void salvarImagem(@RequestParam("fotoDePerfil") MultipartFile foto, @RequestParam Long IdPostagem)
			throws IOException, FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (foto == null) {
			throw new FlopException("O arquivo inserido é inválido.", HttpStatus.BAD_REQUEST);
		}
		postagemService.salvarImagem(foto, IdPostagem, subject.getIdUsuario());
	}

	@Operation(summary = "Inserir nova postagem", description = "Adiciona uma nova postagem ao sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Postagem criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Postagem.class))),
			@ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}"))) })
	@PostMapping(path = "/cadastrar")
	public ResponseEntity<PostagemDTO> cadastrar(@Valid @RequestBody PostagemDTO novaPostagemDTO) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		if (subject.isAdmin() == false) {
			novaPostagemDTO.setUsuarioId(subject.getIdUsuario());
			PostagemDTO postagemCriada = postagemService.cadastrar(novaPostagemDTO);
			return ResponseEntity.status(201).body(postagemCriada);
		} else {
			throw new FlopException("Administradores não podem criar postagens.", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Listar todas as postagens", description = "Retorna uma lista de todas os postagens cadastrados no sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista de postagems retornada com sucesso") })
	@GetMapping(path = "/todos")
	public List<PostagemDTO> pesquisarTodos() throws FlopException {
		return postagemService.pesquisarTodos();
	}

	@Operation(summary = "Excluir postagem")
	@ApiResponse(responseCode = "201", description = "Postagem excluída com sucesso!")
	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		postagemService.excluir(id, subject.getIdUsuario());

		return ResponseEntity.noContent().build();
	}
	
	@Operation(summary = "Pesquisar postagem por ID", description = "Busca uma postagem específica pelo seu ID.")
	@GetMapping(path = "/{id}")
	public ResponseEntity<PostagemDTO> pesquisarPorId(@PathVariable Long id) throws FlopException {
		PostagemDTO postagem = postagemService.pesquisarPorId(id);
		return ResponseEntity.ok(postagem);
	}

	@Operation(summary = "Pesquisar com filtro", description = "Retorna uma lista de postagens de acordo com o filtro selecionado.", responses = {
			@ApiResponse(responseCode = "200", description = "Postagens filtradas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Postagem.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(description = "Detalhes do erro interno", example = "{\"message\": \"Erro interno do servidor\", \"status\": 500}"))) })
	@PostMapping("/filtrar")
	public List<PostagemDTO> pesquisarComFiltros(@RequestBody PostagemSeletor seletor) throws FlopException {
		return postagemService.pesquisarComFiltros(seletor);
	}

	@Operation(summary = "Contador de páginas", description = "Retorna uma contagem com o número total de páginas.")
	@PostMapping("/total-paginas")
	public int contarPaginas(@RequestBody PostagemSeletor seletor) {
		return this.postagemService.contarPaginas(seletor);
	}
}