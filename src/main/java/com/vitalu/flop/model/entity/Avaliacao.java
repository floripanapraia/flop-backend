package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vitalu.flop.model.enums.Condicoes;

import jakarta.persistence.ElementCollection;
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
public class Avaliacao {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idAvaliacao;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	@JsonBackReference("avaliacao-usuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "praia_id", nullable = false)
<<<<<<< Updated upstream
=======
	@JsonBackReference("avaliacao-praia")
>>>>>>> Stashed changes
	private Praia praia;

	@CreationTimestamp
	private LocalDateTime criadoEm;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<Condicoes> condicoes;
}
