package com.vitalu.flop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.SugerirPraia;

@Repository
public interface SugerirPraiaRepository extends JpaRepository<SugerirPraia, Long>, JpaSpecificationExecutor<SugerirPraia> {

}
