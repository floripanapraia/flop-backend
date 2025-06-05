package com.vitalu.flop.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitalu.flop.model.repository.IARepository;
import com.vitalu.flop.service.IAService;

@RestController
@RequestMapping("/ia")
public class IAController {

	private final IAService iaService;
	private final IARepository iaRepository;

	public IAController(IAService iaService, IARepository iaRepository) {
		this.iaService = iaService;
		this.iaRepository = iaRepository;
	}

	@GetMapping("/recomendar")
	public ResponseEntity<?> recomendarPraia(@RequestParam String preferencias) {
		String sql = iaService.gerarQueryComBaseEmPreferencias(preferencias);
		Optional<Map<String, Object>> resultado = iaRepository.executarQuery(sql);

		if (resultado.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma praia encontrada com essas condições.");
		}

		return ResponseEntity.ok(resultado.get());
	}
}