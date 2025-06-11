package com.vitalu.flop.service;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.MailBody;
import com.vitalu.flop.model.entity.TwoFactorAuth;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.TwoFactorAuthRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class TwoFactorAuthService {
    
    @Autowired
    private TwoFactorAuthRepository twoFactorAuthRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Gera e envia código 2FA por email
     */
    @Transactional
    public void generateAndSendTwoFactorCode(String email) throws FlopException {
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        
        // Remove códigos anteriores não utilizados para este usuário
        twoFactorAuthRepository.deleteByUser(user);
        
        // Gera novo código
        int otp = generateOTP();
        
        // Cria registro do 2FA (válido por 5 minutos)
        TwoFactorAuth twoFactorAuth = TwoFactorAuth.builder()
            .otp(otp)
            .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5 minutos
            .user(user)
            .verified(false)
            .build();
        
        twoFactorAuthRepository.save(twoFactorAuth);
        
        // Envia email
        MailBody mailBody = MailBody.builder()
            .to(email)
            .subject("Código de Verificação - Login")
            .text("Olá " + user.getNome() + "!\n\n" +
                  "Seu código de verificação para login é: " + otp + "\n\n" +
                  "Este código expira em 5 minutos.\n\n" +
                  "Se você não tentou fazer login, ignore este email.")
            .build();
        
        emailService.sendSimpleMessage(mailBody);
    }
    
    /**
     * Verifica o código 2FA fornecido
     */
    @Transactional
    public boolean verifyTwoFactorCode(String email, Integer otp) throws FlopException {
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository
            .findByOtpAndUserAndNotVerified(otp, user)
            .orElseThrow(() -> new FlopException("Código de verificação inválido", null));
        
        // Verifica se não expirou
        if (twoFactorAuth.getExpirationTime().before(Date.from(Instant.now()))) {
            twoFactorAuthRepository.deleteById(twoFactorAuth.getId());
            throw new FlopException("Código de verificação expirado", null);
        }
        
        // Marca como verificado
        twoFactorAuth.setVerified(true);
        twoFactorAuthRepository.save(twoFactorAuth);
        
        return true;
    }
    
    /**
     * Verifica se usuário tem código 2FA pendente
     */
    public boolean hasPendingTwoFactorAuth(String email) {
        try {
            Usuario user = usuarioRepository.findByEmail(email)
                .orElse(null);
            
            if (user == null) return false;
            
            return twoFactorAuthRepository.findPendingByUser(user).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Remove códigos expirados (para limpeza automática)
     */
    @Transactional
    public void cleanupExpiredCodes() {
        twoFactorAuthRepository.deleteExpiredTokens(new Date());
    }
    
    private Integer generateOTP() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}