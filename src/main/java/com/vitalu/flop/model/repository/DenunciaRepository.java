package com.vitalu.flop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.vitalu.flop.model.entity.Denuncia;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long>, JpaSpecificationExecutor<Denuncia> {

}
