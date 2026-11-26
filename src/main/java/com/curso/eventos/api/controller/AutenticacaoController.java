package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.LoginRequest;
import com.curso.eventos.api.dto.TokenResponse;
import com.curso.eventos.application.AutenticacaoService;
import com.curso.eventos.application.TokenAcesso;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticacaoService service;

    public AutenticacaoController(AutenticacaoService service) {
        this.service = service;
    }

    @Operation(summary = "Autenticar e emitir JWT")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        TokenAcesso token = service.autenticar(
                request.login(),
                request.senha()
        );

        return new TokenResponse(
                token.valor(),
                "Bearer",
                token.duracaoSegundos(),
                token.expiraEm()
        );
    }
}
