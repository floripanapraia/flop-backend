package com.vitalu.flop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.vitalu.flop.model.dto.DenunciaDTO;
import com.vitalu.flop.model.entity.Denuncia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.StatusDenuncia;
import com.vitalu.flop.model.seletor.DenunciaSeletor;
import com.vitalu.flop.service.DenunciaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/denuncias")
@CrossOrigin(origins = { "http://localhost:3000" }, maxAge = 3600)
public class DenunciaController {

	@Autowired
	private DenunciaService denunciaService;

	@Autowired
	private AuthService authService;

	@Operation(summary = "Inserir nova denuncia", description = "Cria uma nova denuncia", responses = {
			@ApiResponse(responseCode = "200", description = "Denuncia registrada com sucesso"), })
	@PostMapping("/cadastrar")
	public ResponseEntity<Denuncia> cadastrar(@Valid @RequestBody Denuncia denuncia) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		denuncia.setUsuarioDenunciador(subject);

		return ResponseEntity.ok(denunciaService.cadastrar(denuncia));
	}

	@Operation(summary = "Atualizar status da denuncia", description = "Atualiza o status da denuncia", responses = {
			@ApiResponse(responseCode = "200", description = "Status da denuncia atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Denuncia não encontrada") })
	@PutMapping("/atualizar/{idDenuncia}")
	public ResponseEntity<Void> atualizar(@PathVariable Long idDenuncia, @RequestBody StatusDenuncia statusDenuncia)
			throws FlopException {
		denunciaService.atualizar(idDenuncia, statusDenuncia);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Excluir denuncia", description = "Exclui a denuncia", responses = {
			@ApiResponse(responseCode = "200", description = "Denuncia excluida com sucesso"),
			@ApiResponse(responseCode = "400", description = "Denuncia não encontrada") })
	@DeleteMapping("/excluir/{idDenuncia}")
	public ResponseEntity<Void> excluir(@PathVariable Long idDenuncia) throws FlopException {
		Usuario subject = authService.getUsuarioAutenticado();

		denunciaService.excluir(idDenuncia, subject.getIdUsuario());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Pesquisar todas as denúncias", description = "Retorna uma lista com todas as denúncias cadastradas.", responses = {
			@ApiResponse(responseCode = "200", description = "Denuncias retornadas com sucesso."),
			@ApiResponse(responseCode = "400", description = "Denuncias não encontradas."),
			@ApiResponse(responseCode = "401", description = "Usuário não autorizado.") })
	@GetMapping("/todas")
	public List<DenunciaDTO> pesquisarTodas() throws FlopException {
		return denunciaService.pesquisarTodas();
	}

	@Operation(summary = "Pesquisar uma denúncia pelo id", description = "Retorna denuncia especifica para o admin.", responses = {
			@ApiResponse(responseCode = "200", description = "Denuncia retornada com sucesso."),
			@ApiResponse(responseCode = "400", description = "Denuncia não encontrada."),
			@ApiResponse(responseCode = "401", description = "Usuário não autorizado.") })
	@GetMapping("/{id}")
	public ResponseEntity<DenunciaDTO> pesquisarPorId(@PathVariable Long id) throws FlopException {
		DenunciaDTO denuncia = denunciaService.pesquisarPorId(id);
		return ResponseEntity.ok(denuncia);
	}

	@Operation(summary = "Pesquisar com filtro", description = "Retorna uma lista de denúncias de acordo com o filtro selecionado.")
	@PostMapping("/filtrar")
	public List<DenunciaDTO> pesquisarComFiltros(@RequestBody DenunciaSeletor seletor) {
		return denunciaService.pesquisarComFiltros(seletor);
	}

}
