package com.curso.eventos.api;

import com.curso.eventos.application.TokenAcesso;
import com.curso.eventos.application.TokenService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import java.time.Instant;

import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenTest {

    @Autowired
    TokenService tokenService;

    @Autowired
    JwtDecoder decoder;

    @Autowired
    JwtEncoder encoder;

    @Test
    void tokenContemClaimsCorretosEAssinaturaValida() {
        Authentication autenticacao =
                UsernamePasswordAuthenticationToken.authenticated(
                        "operador.eventos",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OPERADOR"))
                );

        TokenAcesso token = tokenService.emitir(autenticacao);
        Jwt jwt = decoder.decode(token.valor());

        assertEquals("https://eventos2026.local", jwt.getIssuer().toString());
        assertEquals("operador.eventos", jwt.getSubject());
        assertNotNull(jwt.getId());
        assertFalse(jwt.getId().isBlank());
        assertTrue(jwt.getClaimAsStringList("perfis").contains("OPERADOR"));
        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());
        assertEquals(
                1800,
                Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).toSeconds()
        );
        assertEquals(1800, token.duracaoSegundos());
    }

    @Test
    void tokenAdulteradoERejeitado() {
        Authentication autenticacao =
                UsernamePasswordAuthenticationToken.authenticated(
                        "operador.eventos",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OPERADOR"))
                );

        TokenAcesso token = tokenService.emitir(autenticacao);

        String[] partes = token.valor().split("[.]");

        assertEquals(3, partes.length);

        char primeiro = partes[2].charAt(0);
        partes[2] = (primeiro == 'A' ? "B" : "A")
                + partes[2].substring(1);

        String adulterado = String.join(".", partes);

        assertThrows(JwtException.class,
                () -> decoder.decode(adulterado));
    }

    @Test
    void tokenExpiradoERejeitado() {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://eventos2026.local")
                .subject("operador.eventos")
                .issuedAt(agora.minusSeconds(600))
                .expiresAt(agora.minusSeconds(180))
                .build();

        String expirado = encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        assertThrows(JwtException.class,
                () -> decoder.decode(expirado));
    }

}
