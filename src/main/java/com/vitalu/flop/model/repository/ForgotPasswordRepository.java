package com.vitalu.flop.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vitalu.flop.model.entity.ForgotPassword;
import com.vitalu.flop.model.entity.Usuario;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

	@Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
	Optional<ForgotPassword> findByOtpAndUser(Integer otp, Usuario user);

}
