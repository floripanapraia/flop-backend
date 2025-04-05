package com.vitalu.flop.model.seletor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.vitalu.flop.model.entity.Sugestao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class SugestaoSeletor extends BaseSeletor implements Specification<Sugestao> {

	private static final long serialVersionUID = 1L;
	private Long idSugestao;
	private String nomePraia;
	private String bairro;
	private String descricao;
	private Boolean analisada;
	private LocalDateTime dataInicioCriacao;
	private LocalDateTime dataFimCriacao;
	private Long idUsuario;

	@Override
	public Predicate toPredicate(Root<Sugestao> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = new ArrayList<>();

		if (this.getIdSugestao() != null) {
			predicates.add(cb.equal(root.get("idSugestao"), this.getIdSugestao()));
		}

		if (this.getNomePraia() != null) {
			predicates.add(filtroDeTexto(cb, root.get("nomePraia"), this.getNomePraia()));
		}

		if (this.getIdUsuario() != null) {
			predicates.add(cb.equal(root.get("usuario").get("idUsuario"), this.getIdUsuario()));
		}

		if (this.getBairro() != null && !this.getBairro().isBlank()) {
			predicates.add(filtroDeTexto(cb, root.get("bairro"), this.getBairro()));
		}

		if (this.getDescricao() != null && !this.getDescricao().isBlank()) {
			predicates.add(filtroDeTexto(cb, root.get("descricao"), this.getDescricao()));
		}

		if (this.getAnalisada() != null) {
			predicates.add(cb.equal(root.get("analisada"), this.getAnalisada()));
		}

		filtrarPorData(root, cb, predicates, this.getDataInicioCriacao(), this.getDataFimCriacao(), "criadaEm");

		return cb.and(predicates.toArray(new Predicate[0]));
	}

}
