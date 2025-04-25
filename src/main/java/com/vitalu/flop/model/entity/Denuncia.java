package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.vitalu.flop.model.dto.DenunciaDTO;
import com.vitalu.flop.model.enums.MotivosDenuncia;
import com.vitalu.flop.model.enums.StatusDenuncia;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
	private LocalDateTime criadoEm;

	@Enumerated(EnumType.STRING)
	private MotivosDenuncia motivo;

	@Enumerated(EnumType.STRING)
	@NotNull
	private StatusDenuncia status = StatusDenuncia.PENDENTE;

	public static DenunciaDTO toDTO(Denuncia denuncia) {
		return new DenunciaDTO(denuncia.getIdDenuncia(), denuncia.getUsuarioDenunciador().getNome(),
				denuncia.getPostagem().getIdPostagem(), denuncia.getPostagem().getMensagem(),
				denuncia.getPostagem().getImagem(), denuncia.getUsuarioDenunciador().getIdUsuario(),
				denuncia.getPostagem().getUsuario().getNome(), denuncia.getMotivo(), denuncia.getStatus(),
				denuncia.getCriadoEm());
	}

}
