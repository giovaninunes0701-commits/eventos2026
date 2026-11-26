package com.curso.eventos.application;

import com.curso.eventos.config.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;

    public TokenService(JwtEncoder encoder, JwtProperties properties) {
        this.encoder = encoder;
        this.properties = properties;
        this.clock = Clock.systemUTC();
    }

    public TokenAcesso emitir(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Autenticacao obrigatoria");
        }

        Instant emitidoEm = clock.instant();
        Instant expiraEm = emitidoEm.plus(properties.expiration());

        List<String> perfis = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(valor -> valor.startsWith("ROLE_"))
                .map(valor -> valor.substring("ROLE_".length()))
                .sorted()
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .subject(authentication.getName())
                .id(UUID.randomUUID().toString())
                .claim("perfis", perfis)
                .build();

        String valor = encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return new TokenAcesso(
                valor,
                expiraEm,
                properties.expiration().toSeconds()
        );
    }

}
