package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Avaliacao {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idAvaliacao;

	private Usuario usuario;

	private Praia praia;

	@CreationTimestamp
	private LocalDateTime dataAvaliacao;
}
