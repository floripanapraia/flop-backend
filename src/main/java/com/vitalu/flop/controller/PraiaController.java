package com.vitalu.flop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.service.PraiaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping(path = "/praia")
@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 3600)
public class PraiaController {
	
	@Autowired
	private PraiaService praiaService;

	@Operation(summary = "Listar todas as praias.", description = "Retorna uma lista de todas as praias cadastrados no sistema.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista de praias retornada com sucesso") })
	@PostMapping(path = "/todos")
	public List<Praia> pesquisarTodos() throws FlopException {
		return praiaService.pesquisarPraiaTodas();
	}
	
}
