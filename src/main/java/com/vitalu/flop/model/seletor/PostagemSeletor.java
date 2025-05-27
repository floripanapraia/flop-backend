package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Postagem;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class PostagemSeletor extends BaseSeletor implements Specification<Postagem> {

	private Long idUsuario;
	private Long idPraia;
	private String mensagem;
	private LocalDateTime criadoEmInicio;
	private LocalDateTime criadoEmFim;

	@Override
	public Predicate toPredicate(Root<Postagem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getMensagem() != null && !this.getMensagem().trim().isEmpty()) {
			predicates.add(cb.like(root.get("mensagem"), "%" + this.getMensagem() + "%"));
		}

		if (this.getIdUsuario() != null) {
			predicates.add(cb.equal(root.get("usuario").get("idUsuario"), this.getIdUsuario()));
		}

		if (this.getIdPraia() != null) {
			predicates.add(cb.equal(root.get("praia").get("idPraia"), this.getIdPraia()));
		}

		if (this.criadoEmInicio != null && this.criadoEmFim != null) {
			filtrarPorData(root, cb, predicates, this.getCriadoEmInicio(), this.getCriadoEmFim(), "criadoEm");
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}