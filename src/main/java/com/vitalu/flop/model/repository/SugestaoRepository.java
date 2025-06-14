package com.vitalu.flop.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Sugestao;

@Repository
public interface SugestaoRepository extends JpaRepository<Sugestao, Long>, JpaSpecificationExecutor<Sugestao> {
	Optional<Sugestao> findByIdSugestao(Long idSugestao);

}
