package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public abstract class BaseSeletor {

	private int pagina;
	private int limite;

	public BaseSeletor() {
		this.limite = 0;
		this.pagina = 0;
	}

	public boolean temPaginacao() {
		return this.limite > 0 && this.pagina > 0;
	}

	public static void filtrarPorData(Root root, CriteriaBuilder cb, List<Predicate> predicates,
			LocalDateTime startDate, LocalDateTime endDate, String attributeName) {
		if (startDate != null && endDate != null) {
			predicates.add(cb.between(root.get(attributeName), startDate, endDate));
		} else if (startDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get(attributeName), startDate));
		} else if (endDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get(attributeName), endDate));
		}
	}
}