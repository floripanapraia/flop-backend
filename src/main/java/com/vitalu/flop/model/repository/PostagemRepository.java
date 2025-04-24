package com.vitalu.flop.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Postagem;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long>, JpaSpecificationExecutor<Postagem> {

	@Query("SELECT p FROM Postagem p WHERE p.praia.idPraia = :praiaId AND p.criadoEm >= :inicioDia AND p.criadoEm <= :fimDia")
	List<Postagem> findPostagensDoDia(@Param("praiaId") Long praiaId, @Param("inicioDia") LocalDateTime inicioDia,
			@Param("fimDia") LocalDateTime fimDia);

	List<Postagem> findByUsuarioIdUsuario(Long idUsuario);
}