package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Avaliacao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class AvaliacaoSeletor extends BaseSeletor implements Specification<Avaliacao> {

	private Long idUsuario;
	private Long idPraia;
	private LocalDateTime criadoEmInicio;
	private LocalDateTime criadoEmFim;

	@Override
	public Predicate toPredicate(Root<Avaliacao> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getIdUsuario() != null) {
			predicates.add(cb.equal(root.get("usuario").get("id"), this.getIdUsuario()));
		}

		if (this.getIdPraia() != null) {
			predicates.add(cb.equal(root.get("praia").get("id"), this.getIdPraia()));
		}
		
		filtrarPorData(root, cb, predicates, this.getCriadoEmInicio(), this.getCriadoEmFim(), "criadoEm");

		return cb.and(predicates.toArray(new Predicate[0]));
	}

}
