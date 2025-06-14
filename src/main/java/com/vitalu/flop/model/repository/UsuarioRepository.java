package com.vitalu.flop.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vitalu.flop.model.entity.Usuario;

import jakarta.transaction.Transactional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
	boolean existsByEmailIgnoreCase(String email);

	Optional<Usuario> findByEmail(String email);

	boolean existsByNickname(String username);

	Optional<Usuario> findById(Long id);

	@Transactional
	@Modifying
	@Query("update Usuario u set u.senha = ?2 where u.email = ?1")
	void updatePassword(String email, String password);
}