package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;

import com.vitalu.flop.model.entity.Denuncia;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.MotivosDenuncia;
import com.vitalu.flop.model.enums.StatusDenuncia;

public class DenunciaMockFactory {

	/**
	 * Cria uma denúncia padrão com status PENDENTE. O denunciador é um "usuário
	 * admin" e a postagem é a "postagem padrão".
	 *
	 * @return uma instância de {@link Denuncia}.
	 */
	public static Denuncia criarDenunciaPendente() {
		Denuncia denuncia = new Denuncia();
		denuncia.setIdDenuncia(1L);
		denuncia.setUsuarioDenunciador(UsuarioMockFactory.criarUsuarioAdmin());
		denuncia.setPostagem(PostagemMockFactory.criarPostagemPadrao());
		denuncia.setCriadoEm(LocalDateTime.now());
		denuncia.setMotivo(MotivosDenuncia.INADEQUADO);
		denuncia.setStatus(StatusDenuncia.PENDENTE);
		return denuncia;
	}

	/**
	 * Cria uma denúncia com status ACEITA.
	 *
	 * @return uma instância de {@link Denuncia} com status ACEITA.
	 */
	public static Denuncia criarDenunciaAceita() {
		Denuncia denuncia = criarDenunciaPendente();
		denuncia.setIdDenuncia(2L);
		denuncia.setStatus(StatusDenuncia.ACEITA);
		denuncia.setMotivo(MotivosDenuncia.SPAM_PROPAGANDA);
		return denuncia;
	}

	/**
	 * Cria uma denúncia com status RECUSADA.
	 *
	 * @return uma instância de {@link Denuncia} com status RECUSADA.
	 */
	public static Denuncia criarDenunciaRecusada() {
		Denuncia denuncia = criarDenunciaPendente();
		denuncia.setIdDenuncia(3L);
		denuncia.setStatus(StatusDenuncia.RECUSADA);
		denuncia.setMotivo(MotivosDenuncia.INCORRETO);
		return denuncia;
	}

	/**
	 * Cria uma denúncia para uma postagem e um denunciador específicos.
	 *
	 * @param postagem    A postagem a ser denunciada.
	 * @param denunciador O usuário que está fazendo a denúncia.
	 * @return uma instância de {@link Denuncia} associada aos parâmetros.
	 */
	public static Denuncia criarDenunciaEspecifica(Postagem postagem, Usuario denunciador) {
		Denuncia denuncia = new Denuncia();
		denuncia.setIdDenuncia(4L);
		denuncia.setUsuarioDenunciador(denunciador);
		denuncia.setPostagem(postagem);
		denuncia.setCriadoEm(LocalDateTime.now());
		denuncia.setMotivo(MotivosDenuncia.VIOLACAO_PRIVACIDADE);
		denuncia.setStatus(StatusDenuncia.PENDENTE);
		return denuncia;
	}
}