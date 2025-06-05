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
	private String imagem;

	@Override
	public Predicate toPredicate(Root<Postagem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getMensagem() != null && !this.getMensagem().trim().isEmpty()) {
			predicates.add(cb.like(root.get("mensagem"), "%" + this.getMensagem() + "%"));
		}

		if (this.getIdUsuario() != null) {
			predicates.add(cb.equal(root.get("usuario").get("usuarioId"), this.getIdUsuario()));
		}

		if (this.getIdPraia() != null) {
			predicates.add(cb.equal(root.get("praia").get("idPraia"), this.getIdPraia()));
		}

		if (this.criadoEmInicio != null && this.criadoEmFim != null) {
			filtrarPorData(root, cb, predicates, this.getCriadoEmInicio(), this.getCriadoEmFim(), "criadoEm");
		}

		if (this.getImagem() != null) {
			String valor = this.getImagem().trim();

			if ("SEM IMAGEM".equalsIgnoreCase(valor)) {
				predicates.add(
						cb.and(
							cb.isNull(root.get("imagem")),
							cb.isNotNull(root.get("mensagem")),
							cb.greaterThan(cb.length(cb.trim(root.get("mensagem"))), 0)));
			} else if ("COM IMAGEM".equalsIgnoreCase(valor)) {
				// Apenas postagens cuja coluna imagem NÃO seja NULL
				predicates.add(cb.isNotNull(root.get("imagem")));
			}
			// Se vier outro valor inesperado, não adiciona filtro de imagem.
		}

		return cb.and(predicates.toArray(new Predicate[0]));
	}
}