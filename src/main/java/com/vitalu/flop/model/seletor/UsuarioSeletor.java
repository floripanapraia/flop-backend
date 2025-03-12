package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Usuario;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class UsuarioSeletor extends BaseSeletor implements Specification<Usuario> {

	private String nome;
	private String email;
	private String username;
	private LocalDateTime criadoEmInicio;
	private LocalDateTime criadoEmFim;

	@Override
	public Predicate toPredicate(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getNome() != null && !this.getNome().trim().isEmpty()) {
			predicates.add(cb.like(root.get("nome"), "%" + this.getNome() + "%"));
		}

		if (this.getEmail() != null && !this.getEmail().trim().isEmpty()) {
			predicates.add(cb.like(root.get("email"), "%" + this.getEmail() + "%"));
		}

		if (this.getUsername() != null && !this.getUsername().trim().isEmpty()) {
			predicates.add(cb.like(root.get("username"), "%" + this.getUsername() + "%"));
		}

		if (this.criadoEmInicio != null && this.criadoEmFim != null) {
			filtrarPorData(root, cb, predicates, this.getCriadoEmInicio(), this.getCriadoEmFim(), "criadoEm");
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}