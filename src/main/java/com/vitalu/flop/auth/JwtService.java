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

	public JwtService(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String getGenerateToken(Authentication authentication) throws FlopException {
		Instant now = Instant.now();

		long dezHorasEmSegundos = 36000L;

		String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("flop") // emissor do token
				.issuedAt(now) // data/hora em que o token foi emitido
				.expiresAt(now.plusSeconds(dezHorasEmSegundos)) // expiração do token, em segundos.
				.subject(authentication.getName()) // nome do usuário
				.claim("roles", roles) // perfis ou permissões (roles)
				.claim("idUsuario", usuarioAutenticado.getIdUsuario()) // mais propriedades adicionais no token
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}