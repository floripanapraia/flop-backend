package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.vitalu.flop.model.enums.MotivosDenuncia;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Denuncia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDenuncia;

  @ManyToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuarioDenunciador;

  @ManyToOne
  @JoinColumn(name = "postagem_id")
  private Postagem postagem;
    
  @CreationTimestamp
  private LocalDateTime dataDenuncia;

	@CreationTimestamp
	private LocalDateTime dataDenuncia;

	@Enumerated(EnumType.STRING)
	private MotivosDenuncia motivo;

	private Boolean analisada;
}
