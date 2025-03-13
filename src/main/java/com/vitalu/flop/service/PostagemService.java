package com.vitalu.flop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class PostagemService {

	@Autowired
	private PostagemRepository postagemRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private AuthService authService;
	@Autowired
	private ImagemService imagemService;

	public Postagem cadastrar(Postagem postagem) throws FlopException {
		Optional<Usuario> autor = usuarioRepository.findById(postagem.getUsuario().getIdUsuario());
		postagem.setUsuario(
				autor.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST)));
		return postagemRepository.save(postagem);
	}

	// uma mensagem deve ser excluída apenas logicamente, permitido apenas para
	// usuário ADMIN ou para USUARIO que criou a postagem
	public void excluir(Long idPostagem, Long idUsuario) throws FlopException {
		Postagem postagem = postagemRepository.findById(idPostagem).orElseThrow(
				() -> new FlopException("A postagem selecionada não foi encontrada.", HttpStatus.BAD_REQUEST));
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		boolean isAdmin = usuario.isAdmin();
		boolean isOwner = postagem.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

		if (!isAdmin && !isOwner) {
			throw new FlopException("Você não é o dono desta postagem, portanto não pode excluí-la.",
					HttpStatus.FORBIDDEN);
		}

		postagemRepository.save(postagem);
	}

}
