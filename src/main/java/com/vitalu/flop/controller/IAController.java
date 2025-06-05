package com.vitalu.flop.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.ia.IAService;

@RestController
@RequestMapping("/ia")
public class IAController {

	private final IAService iaService;

	public IAController(IAService iaService) {
		this.iaService = iaService;
	}

	@PostMapping("/gerar-query")
	public ResponseEntity<String> gerarQuery(@RequestBody Map<String, String> body) {
		String preferencias = body.get("preferencias");
		String query = iaService.gerarClausulaSQL(preferencias);
		return ResponseEntity.ok(query);
	}
}