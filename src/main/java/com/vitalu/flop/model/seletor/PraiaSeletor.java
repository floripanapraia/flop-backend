package com.vitalu.flop.model.seletor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.enums.Condicoes;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class PraiaSeletor extends BaseSeletor implements Specification<Praia> {

	private static final long serialVersionUID = 1L;
	private String nomePraia;
	private List<Condicoes> condicoes;

	@Override
	public Predicate toPredicate(Root<Praia> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getNomePraia() != null) {
			predicates.add(filtroDeTexto(cb, root.get("nomePraia"), this.getNomePraia()));
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}

}
