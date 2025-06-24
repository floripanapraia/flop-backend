package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;

public class PostagemMockFactory {

	/**
	 * Cria uma postagem padrão com dados genéricos. O usuário é o "Ayrton Senna" e
	 * a praia é "Praia de Copacabana".
	 *
	 * @return uma instância de {@link Postagem}.
	 */
	public static Postagem criarPostagemPadrao() {
		Postagem postagem = new Postagem();
		postagem.setIdPostagem(1L);
		postagem.setUsuario(UsuarioMockFactory.criarUsuarioPadrao());
		postagem.setPraia(PraiaMockFactory.criarPraiaPadrao());
		postagem.setCriadoEm(LocalDateTime.now());
		postagem.setImagem(
				"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/wAALCAABAAEBAREA/8QAFAABAAAAAAAAAAAAAAAAAAAACv/EABQQAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQEAAD8AlAAAAAD/2gAIAQEAAT8A//Z");
		postagem.setMensagem("Dia incrível na praia hoje! O mar está perfeito para um mergulho.");
		postagem.setExcluida(false);
		postagem.setDenuncias(new ArrayList<>());
		return postagem;
	}

	/**
	 * Cria uma postagem que foi marcada como excluída.
	 *
	 * @return uma instância de {@link Postagem} com o campo 'excluida' como true.
	 */
	public static Postagem criarPostagemExcluida() {
		Postagem postagem = criarPostagemPadrao();
		postagem.setIdPostagem(2L);
		postagem.setExcluida(true);
		postagem.setMensagem("Esta postagem foi removida.");
		return postagem;
	}

	/**
	 * Cria uma postagem para um usuário e praia específicos.
	 *
	 * @param usuario O usuário que criou a postagem.
	 * @param praia   A praia onde a postagem foi feita.
	 * @return uma instância de {@link Postagem} associada aos parâmetros.
	 */
	public static Postagem criarPostagemComUsuarioEPraia(Usuario usuario, Praia praia) {
		Postagem postagem = new Postagem();
		postagem.setIdPostagem(3L);
		postagem.setUsuario(usuario);
		postagem.setPraia(praia);
		postagem.setCriadoEm(LocalDateTime.now().minusDays(1));
		postagem.setImagem(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+P+/HgAFhAJ/wlseKgAAAABJRU5ErkJggg==");
		postagem.setMensagem("Que vista maravilhosa!");
		postagem.setExcluida(false);
		postagem.setDenuncias(new ArrayList<>());
		return postagem;
	}

	/**
	 * Cria uma postagem sem imagem, apenas com texto.
	 *
	 * @return uma instância de {@link Postagem} sem imagem.
	 */
	public static Postagem criarPostagemSemImagem() {
		Postagem postagem = criarPostagemPadrao();
		postagem.setIdPostagem(4L);
		postagem.setImagem(null);
		postagem.setMensagem("Cuidado com as águas-vivas hoje!");
		return postagem;
	}

}