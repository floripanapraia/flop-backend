package com.vitalu.flop.model.mock;

import java.time.LocalDateTime;

import com.vitalu.flop.model.entity.Denuncia;
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
}