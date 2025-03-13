package com.vitalu.flop.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
	boolean existsByEmailIgnoreCase(String email);

	Optional<Usuario> findByEmail(String email);

	boolean existsByUsername(String username);

	Optional<Usuario> findById(Long id);
}