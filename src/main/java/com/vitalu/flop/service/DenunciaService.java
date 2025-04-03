package com.vitalu.flop.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.DenunciaDTO;
import com.vitalu.flop.model.entity.Denuncia;
import com.vitalu.flop.model.entity.Postagem;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.MotivosDenuncia;
import com.vitalu.flop.model.enums.StatusDenuncia;
import com.vitalu.flop.model.repository.DenunciaRepository;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.DenunciaSeletor;

@Service
public class DenunciaService {
	@Autowired
	private DenunciaRepository denunciaRepository;

	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Denuncia cadastrar(Denuncia denuncia) throws FlopException {
		if (denuncia.getMotivo() == null || !EnumSet.allOf(MotivosDenuncia.class).contains(denuncia.getMotivo())) {
			throw new FlopException("Motivo de denúncia inválido.", HttpStatus.BAD_REQUEST);
		}

		Postagem postDenunciado = postagemRepository.findById(denuncia.getPostagem().getIdPostagem())
				.orElseThrow(() -> new FlopException("Postagem não encontrada.", HttpStatus.BAD_REQUEST));
		Usuario autorDaDenuncia = usuarioRepository.findById(denuncia.getUsuarioDenunciador().getIdUsuario())
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST));

		denuncia.setPostagem(postDenunciado);
		denuncia.setUsuarioDenunciador(autorDaDenuncia);

		return denunciaRepository.save(denuncia);
	}

//TODO dando erro enum
	public void atualizar(Long idDenuncia, StatusDenuncia novoStatus) throws FlopException {
		Denuncia denuncia = denunciaRepository.findById(idDenuncia)
				.orElseThrow(() -> new FlopException("Denúncia não encontrada.", HttpStatus.BAD_REQUEST));
		Postagem postagem = denuncia.getPostagem();

		atualizarStatusPruu(postagem, denuncia.getStatus(), novoStatus);

		denuncia.setStatus(novoStatus);
		denunciaRepository.save(denuncia);
	}

	// Não será possivel excluir uma denuncia.
	public void excluir(Long idDenuncia, Long idUsuario) throws FlopException {
		Denuncia denuncia = denunciaRepository.findById(idDenuncia)
				.orElseThrow(() -> new FlopException("A denúncia buscada não foi encontrada.", HttpStatus.BAD_REQUEST));

		if (denuncia.getUsuarioDenunciador().getIdUsuario().equals(idUsuario)) {
			denunciaRepository.deleteById(idDenuncia);
		} else {
			throw new FlopException("Não é possível excluir denúncias que não foram feitas por você.",
					HttpStatus.BAD_REQUEST);
		}
	}

	public List<DenunciaDTO> pesquisarTodas() {
		List<Denuncia> denuncias = denunciaRepository.findAll();
		return toDenunciaDTO(denuncias);
	}

	public DenunciaDTO pesquisarPorId(Long idDenuncia) throws FlopException {
		Denuncia denuncia = denunciaRepository.findById(idDenuncia)
				.orElseThrow(() -> new FlopException("A denúncia buscada não foi encontrada.", HttpStatus.BAD_REQUEST));
		return Denuncia.toDTO(denuncia);
	}

	// TODO está listando todas denuncias
	public List<DenunciaDTO> pesquisarComFiltros(DenunciaSeletor seletor) {
		List<Denuncia> denuncias;

		if (seletor.temPaginacao()) {
			int pageNumber = seletor.getPagina();
			int pageSize = seletor.getLimite();

			PageRequest page = PageRequest.of(pageNumber - 1, pageSize);
			denuncias = denunciaRepository.findAll(seletor, page).toList();
		}

		denuncias = denunciaRepository.findAll(seletor);

		return toDenunciaDTO(denuncias);
	}

	public List<DenunciaDTO> toDenunciaDTO(List<Denuncia> denuncias) {
		List<DenunciaDTO> denunciasDTO = new ArrayList<>();

		for (Denuncia d : denuncias) {
			DenunciaDTO dto = Denuncia.toDTO(d);
			denunciasDTO.add(dto);

		}
		return denunciasDTO;
	}

	void atualizarStatusPruu(Postagem postagem, StatusDenuncia statusAtual, StatusDenuncia novoStatus) {
		if (novoStatus == StatusDenuncia.ACEITA) {
			postagem.setExcluida(true);
		} else if (statusAtual == StatusDenuncia.ACEITA
				&& (novoStatus == StatusDenuncia.RECUSADA || novoStatus == StatusDenuncia.PENDENTE)) {
			postagem.setExcluida(false);
		}

		postagemRepository.save(postagem);
	}

}
