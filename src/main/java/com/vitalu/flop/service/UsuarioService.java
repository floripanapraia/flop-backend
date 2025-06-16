package com.vitalu.flop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vitalu.flop.auth.AuthService;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.mapper.UsuarioMapper;
import com.vitalu.flop.model.dto.UsuarioDTO;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.PostagemRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.UsuarioSeletor;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private ImagemService imagemService;
	@Autowired
	private PostagemService postagemService;
	@Autowired
	private AuthService authService;

	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private UsuarioMapper usuarioMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

		// Verifica se o usuário está bloqueado
		if (usuario.isBloqueado()) {
			throw new DisabledException("Esta conta está bloqueada. Entre em contato com o suporte.");
		}

		return usuario;
	}

	public void cadastrar(Usuario usuario) throws FlopException {
		if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
			throw new FlopException("O e-mail informado já está cadastrado.", HttpStatus.BAD_REQUEST);
		}
		if (usuarioRepository.existsByNickname(usuario.getUsername())) {
			throw new FlopException("O username informado já está registrado.", HttpStatus.BAD_REQUEST);
		}
		usuarioRepository.save(usuario);
	}

	public Usuario atualizar(Usuario usuarioASerAtualizado) throws FlopException {
		Usuario usuarioExistente = usuarioRepository.findById(usuarioASerAtualizado.getIdUsuario())
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		usuarioExistente.setNome(usuarioASerAtualizado.getNome());
		usuarioExistente.setEmail(usuarioASerAtualizado.getEmail());
		usuarioExistente.setFotoPerfil(usuarioASerAtualizado.getFotoPerfil());

		if (usuarioASerAtualizado.getSenha() != null && !usuarioASerAtualizado.getSenha().isEmpty()
				&& !usuarioASerAtualizado.getSenha().equals("NO_PASSWORD_UPDATE")) {
			usuarioExistente.setSenha(encoder.encode(usuarioASerAtualizado.getSenha()));
		}

		return usuarioRepository.save(usuarioExistente);
	}

	public void excluir(Long idUsuario) throws FlopException {
		usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		// Deleta todas as postagens do usuário antes de deletar o usuário
		postagemService.excluirPostagensDoUsuario(idUsuario);

		usuarioRepository.deleteById(idUsuario);
	}

	public List<UsuarioDTO> pesquisarTodos() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios.stream().map(usuarioMapper::toDTO).collect(Collectors.toList());
	}

	public UsuarioDTO pesquisarPorId(Long id) throws FlopException {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
		return usuarioMapper.toDTO(usuario);
	}

	public List<UsuarioDTO> pesquisarComFiltros(UsuarioSeletor seletor) {
		List<Usuario> usuarios;
		if (seletor.temPaginacao()) {
			PageRequest page = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite());
			usuarios = usuarioRepository.findAll(seletor, page).toList();
		} else {
			usuarios = usuarioRepository.findAll(seletor);
		}
		return usuarios.stream().map(usuarioMapper::toDTO).collect(Collectors.toList());
	}

	public Usuario buscarPorEmail(String email) throws FlopException {
	    return usuarioRepository.findByEmail(email)
	            .orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
	}
	
	public void salvarFotoDePerfil(MultipartFile foto, Long usuarioId) throws FlopException {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
		String imagemBase64 = imagemService.processarImagem(foto);
		usuario.setFotoPerfil(imagemBase64);
		usuarioRepository.save(usuario);
	}

	public Usuario bloquearUsuario(Long idUsuario, boolean bloquear) throws FlopException {
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		// Não permitir que um administrador bloqueie a si mesmo
		Usuario adminAutenticado = authService.getUsuarioAutenticado();
		if (usuario.getIdUsuario().equals(adminAutenticado.getIdUsuario())) {
			throw new FlopException("Não é possível bloquear sua própria conta.", HttpStatus.BAD_REQUEST);
		}

		usuario.setBloqueado(bloquear);
		return usuarioRepository.save(usuario);
	}

}