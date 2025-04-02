package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Denuncia;
import com.vitalu.flop.model.enums.MotivosDenuncia;
import com.vitalu.flop.model.enums.StatusDenuncia;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class DenunciaSeletor extends BaseSeletor implements Specification<Denuncia> {

	private Long idUsuario;
	private Long idPostagem;
	private MotivosDenuncia motivo;
	private StatusDenuncia status;
	private LocalDateTime criadoEmInicio;
	private LocalDateTime criadoEmFim;

	@Override
	public Predicate toPredicate(Root<Denuncia> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getIdUsuario() != null) {
			predicates.add(cb.equal(root.get("usuario").get("id"), this.getIdUsuario()));
		}

		if (this.getIdPostagem() != null) {
			predicates.add(cb.equal(root.get("pruu").get("id"), this.getIdPostagem()));
		}

		if (this.getMotivo() != null) {
			predicates.add(cb.equal(root.get("motivo"), this.getMotivo()));
		}

		if (this.getStatus() != null) {
			predicates.add(cb.equal(root.get("status"), this.getStatus()));
		}

		if (this.criadoEmInicio != null && this.criadoEmFim != null) {
			filtrarPorData(root, cb, predicates, this.getCriadoEmInicio(), this.getCriadoEmFim(), "criadoEm");
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}