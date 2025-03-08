package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class SugerirPraia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idSugestao;
	@NotNull(message = "É obrigatório informar o nome da praia.")
	private String nomePraia;
	@NotNull(message = "É obrigatório informar o nome do bairro.")
	private String bairro;
	@NotNull(message = "É obrigatório informar a descrição da praia.")
	private String descricao;
	private Boolean analisada;
	@CreationTimestamp
	private LocalDateTime criadaEm;
	@ManyToOne
	@JoinColumn(name = "idUsuario")
	private Usuario usuario;
}
