package com.vitalu.flop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.UsuarioSeletor;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

//    @Autowired
//    private ImagemService imagemService;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return (UserDetails) usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado " + username));
	}

	public void cadastrar(Usuario usuario) throws FlopException {
		if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
			throw new FlopException("O e-mail informado já está cadastrado. Por favor, utilize um e-mail diferente.",
					HttpStatus.BAD_REQUEST);
		}

		if (usuarioRepository.existsByUsername(usuario.getUsername())) {
			throw new FlopException(
					"O username informado já está registrado. Por favor, verifique os dados ou utilize outro CPF.",
					HttpStatus.BAD_REQUEST);
		}

		usuarioRepository.save(usuario);
	}

	public void cadastrarAdmin(Usuario usuario) throws FlopException {
		if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
			throw new FlopException("O e-mail informado já está cadastrado. Por favor, utilize um e-mail diferente.",
					HttpStatus.BAD_REQUEST);
		}

		if (usuarioRepository.existsByUsername(usuario.getUsername())) {
			throw new FlopException(
					"O username informado já está registrado. Por favor, verifique os dados ou utilize outro CPF.",
					HttpStatus.BAD_REQUEST);
		}

		usuarioRepository.save(usuario);
	}

	public Usuario atualizar(Usuario usuarioASerAtualizado) throws FlopException {
		Usuario usuarioExistente = usuarioRepository.findById(usuarioASerAtualizado.getIdUsuario())
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		usuarioExistente.setNome(usuarioASerAtualizado.getNome());
		usuarioExistente.setEmail(usuarioASerAtualizado.getEmail());

		if (usuarioASerAtualizado.getSenha() != null && !usuarioASerAtualizado.getSenha().isEmpty()) {
			usuarioExistente.setSenha(encoder.encode(usuarioASerAtualizado.getSenha()));
		}

		return usuarioRepository.save(usuarioExistente);
	}

	public void excluir(String id) throws FlopException {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		if (usuario.getPostagem().isEmpty()) {
			usuarioRepository.deleteById(id);
		} else {
			throw new FlopException("Usuários com posts criados não podem ser deletados.", HttpStatus.BAD_REQUEST);
		}
	}

	public List<Usuario> pesquisarTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario pesquisarPorId(String id) throws FlopException {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
	}

	public List<Usuario> pesquisarComFiltros(UsuarioSeletor seletor) {
		if (seletor.temPaginacao()) {
			int pageNumber = seletor.getPagina();
			int pageSize = seletor.getLimite();

			PageRequest page = PageRequest.of(pageNumber - 1, pageSize);
			return usuarioRepository.findAll(seletor, page).toList();
		}

		return usuarioRepository.findAll(seletor);
	}

//    public void salvarFotoDePerfil(MultipartFile foto, String usuarioId) throws FlopException {
//        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
//        String imagemBase64 = imagemService.processarImagem(foto);
//        usuario.setFotoDePerfil(imagemBase64);
//        System.out.println(imagemBase64);
//        usuarioRepository.save(usuario);
//    }

}