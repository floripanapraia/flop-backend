package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUsuario;

	@NotBlank(message = "O nome não pode estar em branco.")
	@Size(max = 200, message = "O nome deve ter entre 3 e 200 caracteres.")
	private String nome;

	private String username;

	@NotBlank(message = "A senha não deve estar em branco.")
	@Size(max = 500)
	private String senha;

	@NotBlank(message = "O email não pode estar em branco.")
	@Email(message = "O email informado deve ser válido.")
	@Column(unique = true)
	private String email;

	@NotNull(message = "É obrigatório informar se o usuário é administrador.")
	private boolean isAdmin;

	@CreationTimestamp
	private LocalDateTime criadoEm;

	@Column(columnDefinition = "LONGTEXT")
	private String fotoPerfil;

	private boolean bloqueado = false;

//	private Localizacao localizacao;

	@OneToMany(mappedBy = "usuario")
	@JsonBackReference(value = "usuario-avaliacoes")
	private List<Avaliacao> avaliacao;

	@OneToMany(mappedBy = "usuario")
	@JsonBackReference(value = "usuario-postagens")
	private List<Postagem> postagem;

	@OneToMany(mappedBy = "usuario")
	@JsonBackReference(value = "usuario-sugestoes")
	private List<Sugestao> sugestoes;

}
