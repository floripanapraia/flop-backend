package com.vitalu.flop.model.repository;

import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.vitalu.flop.model.entity.TwoFactorAuth;
import com.vitalu.flop.model.entity.Usuario;

public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, Integer> {
    
    @Query("SELECT tfa FROM TwoFactorAuth tfa WHERE tfa.otp = ?1 AND tfa.user = ?2 AND tfa.verified = false")
    Optional<TwoFactorAuth> findByOtpAndUserAndNotVerified(Integer otp, Usuario user);
    
    @Query("SELECT tfa FROM TwoFactorAuth tfa WHERE tfa.user = ?1 AND tfa.verified = false")
    Optional<TwoFactorAuth> findPendingByUser(Usuario user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactorAuth tfa WHERE tfa.expirationTime < ?1")
    void deleteExpiredTokens(Date now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactorAuth tfa WHERE tfa.user = ?1")
    void deleteByUser(Usuario user);
}