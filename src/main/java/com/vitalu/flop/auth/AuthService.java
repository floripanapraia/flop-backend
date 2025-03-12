package com.vitalu.flop.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class AuthService {
    private final JwtService jwtService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public AuthService(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
    }

    public String authenticate(Authentication authentication) throws FlopException {
        return jwtService.getGenerateToken(authentication);
    }

    public Usuario getUsuarioAutenticado() throws FlopException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            Jwt jwt = (Jwt) principal;
            String login = jwt.getClaim("sub");

            usuarioAutenticado = usuarioRepository.findByEmail(login).orElseThrow(() -> new FlopException("Usuário não encontrado", HttpStatus.BAD_REQUEST));
        }

        if (usuarioAutenticado == null) {
            throw new FlopException("Usuário não encontrado", HttpStatus.BAD_REQUEST);
        }

        return usuarioAutenticado;
    }
}