package com.vitalu.flop.auth;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.repository.UsuarioRepository;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String getGenerateToken(Authentication authentication) throws FlopException {
        Instant now = Instant.now();
        long dezHorasEmSegundo = 36000L;

        String perfil = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Object principal = authentication.getPrincipal();
        Usuario authenticatedUser;

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            String login = jwt.getSubject();

            authenticatedUser = usuarioRepository.findByEmail(login).orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
        } else {
            authenticatedUser = (Usuario) principal;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("flop").issuedAt(now).expiresAt(now.plusSeconds(dezHorasEmSegundo)).subject(authentication.getName()).claim("perfil", perfil).claim("idUsuario", authenticatedUser.getIdUsuario()).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}