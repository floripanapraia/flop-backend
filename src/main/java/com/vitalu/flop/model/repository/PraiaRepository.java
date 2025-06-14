package com.vitalu.flop.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.enums.Condicoes;

@Repository
public interface PraiaRepository extends JpaRepository<Praia, Long>, JpaSpecificationExecutor<Praia> {

	@Query("""
			  SELECT a.praia FROM Avaliacao a
			  JOIN a.condicoes c
			  WHERE a.criadoEm BETWEEN :inicio AND :fim
			  AND c IN :condicoes
			  GROUP BY a.praia
			  HAVING COUNT(DISTINCT c) >= :qtdCondicoes
			""")
	List<Praia> findPraiasComCondicoesHoje(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
			@Param("condicoes") List<Condicoes> condicoes, @Param("qtdCondicoes") long qtdCondicoes);

}
