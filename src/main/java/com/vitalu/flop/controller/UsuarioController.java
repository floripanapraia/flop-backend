package com.vitalu.flop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.mapper.UsuarioMapper;
import com.vitalu.flop.model.dto.UsuarioDTO;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.seletor.UsuarioSeletor;
import com.vitalu.flop.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/usuarios")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
@MultipartConfig(fileSizeThreshold = 10485760)
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private AuthService authService;

	@Autowired
	private UsuarioMapper usuarioMapper;

	@Operation(summary = "Salvar foto de perfil", description = "Salva a foto de perfil do usuário autenticado.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Foto de perfil salva com sucesso"),
			@ApiResponse(responseCode = "400", description = "Arquivo inválido ou ausente"),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado") })
	@PostMapping("/salvar-foto")
	public void salvarFotoDePerfil(@RequestParam("fotoDePerfil") MultipartFile foto) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();
		if (foto == null) {
			throw new FlopException("O arquivo inserido é inválido.", HttpStatus.BAD_REQUEST);
		}
		usuarioService.salvarFotoDePerfil(foto, subject.getIdUsuario());
	}

	@Operation(summary = "Atualizar dados do usuário", description = "Atualiza os dados do usuário autenticado com as informações fornecidas.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Erro de validação nos dados fornecidos"),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado") })
	@PutMapping(path = "/atualizar")
	public ResponseEntity<Usuario> atualizar(@Valid @RequestBody Usuario usuarioASerAtualizado) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		usuarioASerAtualizado.setIdUsuario(subject.getIdUsuario());
		usuarioASerAtualizado.setNickname(subject.getNickname());

		Usuario usuarioAtualizado = usuarioService.atualizar(usuarioASerAtualizado);

		return ResponseEntity.ok(usuarioAtualizado);
	}

	@Operation(summary = "Excluir conta de usuário", description = "Exclui a conta do usuário autenticado de forma permanente.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado") })
	@DeleteMapping(path = "/excluir")
	public ResponseEntity<Void> excluir() throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		usuarioService.excluir(subject.getIdUsuario());

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Listar todos os usuários", description = "Retorna uma lista completa com todos os usuários cadastrados.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso") })
	@GetMapping("/todos")
	public ResponseEntity<List<UsuarioDTO>> pesquisarTodos() {
		List<UsuarioDTO> usuariosDTO = usuarioService.pesquisarTodos();
		return ResponseEntity.ok(usuariosDTO);
	}

	@Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário com base no ID informado.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado") })
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> pesquisarPorId(@PathVariable Long id) throws FlopException {
		UsuarioDTO usuarioDTO = usuarioService.pesquisarPorId(id);
		return ResponseEntity.ok(usuarioDTO);
	}

	@Operation(summary = "Filtrar usuários", description = "Filtra usuários com base nos critérios definidos no seletor.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuários filtrados com sucesso") })
	@PostMapping("/filtrar")
	public ResponseEntity<List<UsuarioDTO>> pesquisarComFiltros(@RequestBody UsuarioSeletor seletor) {
		List<UsuarioDTO> usuariosDTO = usuarioService.pesquisarComFiltros(seletor);
		return ResponseEntity.ok(usuariosDTO);
	}

	@Operation(summary = "Obter usuário autenticado", description = "Retorna os dados do usuário atualmente autenticado.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dados do usuário autenticado retornados com sucesso"),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado") })
	@GetMapping("/usuario-autenticado")
	public ResponseEntity<UsuarioDTO> buscarUsuarioAutenticado() throws FlopException {
		Usuario usuario = authService.getUsuarioAutenticado();

		UsuarioDTO dto = usuarioMapper.toDTO(usuario);

		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Bloquear usuário", description = "Bloqueia ou desbloqueia um usuário específico. Apenas administradores podem usar este recurso.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuário bloqueado/desbloqueado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
			@ApiResponse(responseCode = "403", description = "Usuário sem permissão de administrador"),
			@ApiResponse(responseCode = "404", description = "Usuário não encontrado") })
	@PutMapping("/bloquear/{id}")
	public ResponseEntity<UsuarioDTO> bloquearUsuario(@PathVariable Long id, @RequestParam boolean bloquear)
			throws FlopException {
		Usuario usuarioAtualizado = usuarioService.bloquearUsuario(id, bloquear);
		UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuarioAtualizado);
		return ResponseEntity.ok(usuarioDTO);
	}

}