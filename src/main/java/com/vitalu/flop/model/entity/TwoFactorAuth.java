package com.vitalu.flop.model.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TwoFactorAuth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer otp;
    
    @Column(nullable = false)
    private Date expirationTime;
    
    // CORRIGIDO: Adicionado JoinColumn para mapear corretamente a chave estrangeira
    @OneToOne
    @JoinColumn(name = "user_id_usuario", nullable = false)
    private Usuario user;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean verified = false;
    
    @Builder.Default
    @Column(nullable = false)
    private Date createdAt = new Date();
}
