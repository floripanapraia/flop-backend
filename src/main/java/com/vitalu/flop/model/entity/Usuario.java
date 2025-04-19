package com.vitalu.flop.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vitalu.flop.model.dto.UsuarioDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
	private String username;

	@NotBlank(message = "Por favor, preencha a senha.")
	@Size(max = 500, message = "A senha deve ter no máximo 500 caracteres.")
	@JsonIgnore
	private String senha;

	@NotBlank(message = "Por favor, preencha o e-mail.")
	@Email(message = "Informe um e-mail válido.")
	@Column(unique = true)
	private String email;

	@NotNull(message = "Informe se o usuário é administrador ou não.")
	private boolean isAdmin;

	@CreationTimestamp
	private LocalDateTime criadoEm;

	@Column(columnDefinition = "LONGTEXT")
	private String fotoPerfil;

	private boolean bloqueado = false;

//	private Localizacao localizacao;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "avaliacao-usuario")
	private List<Avaliacao> avaliacao;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "postagem-usuario")
	private List<Postagem> postagem;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
	@JsonManagedReference(value = "usuario-sugestoes")
	private List<Sugestao> sugestoes;

	// Métodos da interface UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

		list.add(new SimpleGrantedAuthority("USER"));

		return list;
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	public UsuarioDTO toDTO() {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setIdUsuario(this.idUsuario);
		dto.setNome(this.nome);
		dto.setUsername(this.username);
		dto.setEmail(this.email);
		dto.setSenha(this.senha);
		dto.setAdmin(this.isAdmin);
		dto.setDataCriacao(this.criadoEm);
		dto.setFotoPerfil(this.fotoPerfil);
		dto.setBloqueado(this.bloqueado);

		// Mapeando IDs das listas
//	    if (this.avaliacao != null) {
//	        dto.setAvaliacaoIds(this.avaliacao.stream()
//	            .map(a -> a.getIdAvaliacao())
//	            .toList());
//	    } else {
//	        dto.setAvaliacaoIds(new ArrayList<>());
//	    }
//
//	    if (this.postagem != null) {
//	        dto.setPostagemIds(this.postagem.stream()
//	            .map(p -> p.getIdPostagem())
//	            .toList());
//	    } else {
//	        dto.setPostagemIds(new ArrayList<>());
//	    }
//
//	    if (this.sugestoes != null) {
//	        dto.setSugerirPraiaIds(this.sugestoes.stream()
//	            .map(s -> s.getIdSugestao())
//	            .toList());
//	    } else {
//	        dto.setSugerirPraiaIds(new ArrayList<>());
//	    }

		return dto;
	}

}
