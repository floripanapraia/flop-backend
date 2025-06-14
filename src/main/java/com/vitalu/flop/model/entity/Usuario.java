package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUsuario;

	@NotBlank(message = "Por favor, preencha o nome.")
	@Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres.")
	private String nome;

	@NotBlank(message = "Por favor, preencha o nome de usuário.")
	@Size(min = 3, max = 15, message = "O nome de usuário deve ter entre 3 e 15 caracteres.")
	@Column(unique = true)
	private String nickname;

	@NotBlank(message = "Por favor, preencha a senha.")
	@Size(max = 500, message = "A senha deve ter no máximo 500 caracteres.")
	private String senha;

	@NotBlank(message = "Por favor, preencha o e-mail.")
	@Email(message = "Informe um e-mail válido.")
	@Column(unique = true)
	private String email;

	@NotNull(message = "Informe se o usuário é administrador ou não.")
	private boolean isAdmin = false;

	@CreationTimestamp
	private LocalDateTime criadoEm;

	@Column(columnDefinition = "LONGTEXT")
	private String fotoPerfil;

	private boolean bloqueado = false;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "avaliacao-usuario")
	private List<Avaliacao> avaliacao;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "postagem-usuario")
	private List<Postagem> postagem;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "usuario-sugestoes")
	private List<Sugestao> sugestoes;

	@OneToOne(mappedBy = "user")
	private ForgotPassword forgotPassword;
	
	// ADICIONADO: Relacionamento com TwoFactorAuth
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private TwoFactorAuth twoFactorAuth;

	// Métodos da interface UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.isAdmin ? "ADMIN" : "USER"));
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

}
