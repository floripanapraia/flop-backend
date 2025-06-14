package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vitalu.flop.model.dto.PostagemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "postagem")
@Data
public class Postagem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "id_postagem")
	private Long idPostagem;

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	@JsonBackReference("postagem-usuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "praia_id", nullable = false)
	@JsonBackReference("postagem-praia")
	private Praia praia;

	@CreationTimestamp
	private LocalDateTime criadoEm;

	@Column(columnDefinition = "LONGTEXT")
	private String imagem;

	@Size(max = 300)
	private String mensagem;

	private Boolean excluida;

	@OneToMany(mappedBy = "postagem")
	@JsonBackReference
	private List<Denuncia> denuncias;

	public static PostagemDTO toDTO(Postagem postagem) {
		return new PostagemDTO(postagem.getIdPostagem(), postagem.getUsuario().getIdUsuario(),
				postagem.getUsuario().getFotoPerfil(), postagem.getUsuario().getNickname(),
				postagem.getPraia().getIdPraia(), postagem.getPraia().getNomePraia(), postagem.getCriadoEm(),
				postagem.getImagem(), postagem.getMensagem(), postagem.getExcluida());
	}
}
