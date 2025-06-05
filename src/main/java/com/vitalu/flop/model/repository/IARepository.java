package com.vitalu.flop.model.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class IARepository {

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public Optional<Map<String, Object>> executarQuery(String sqlGerado) {
		try {
			Query query = entityManager.createNativeQuery(sqlGerado);
			List<Object[]> resultado = query.getResultList();

			if (resultado.isEmpty())
				return Optional.empty();

			Object[] linha = resultado.get(0);

			Map<String, Object> dados = new HashMap<>();
			dados.put("nome_praia", linha[0]);
			dados.put("total_avaliacoes", linha[1]);

			return Optional.of(dados);

		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
