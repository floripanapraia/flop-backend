package com.vitalu.flop.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Avaliacao;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>, JpaSpecificationExecutor<Avaliacao> {

	@Query("SELECT a FROM Avaliacao a WHERE a.praia.idPraia = :praiaId AND a.criadoEm >= :inicioDia AND a.criadoEm <= :fimDia")
	List<Avaliacao> findAvaliacoesDoDia(@Param("praiaId") Long praiaId, @Param("inicioDia") LocalDateTime inicioDia,
			@Param("fimDia") LocalDateTime fimDia);

}
