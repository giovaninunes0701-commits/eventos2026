package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.AlterarSenhaRequest;
import com.curso.eventos.api.dto.ContaAcessoRequest;
import com.curso.eventos.api.dto.ContaAcessoResponse;
import com.curso.eventos.application.ContaAcessoService;
import com.curso.eventos.domain.ContaAcesso;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contas-acesso")
public class ContaAcessoController {

    private final ContaAcessoService service;

    public ContaAcessoController(ContaAcessoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar conta de acesso")
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<ContaAcessoResponse> cadastrar(
            @Valid @RequestBody ContaAcessoRequest request) {

        ContaAcesso conta = service.cadastrar(
                request.pessoaId(),
                request.login(),
                request.senha()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paraResponse(conta));
    }

    private ContaAcessoResponse paraResponse(ContaAcesso conta) {
        return new ContaAcessoResponse(
                conta.getId(),
                conta.getPessoa().getId(),
                conta.getPessoa().getNome(),
                conta.getLogin(),
                conta.getStatus(),
                conta.getPerfis()
        );
    }


    @Operation(summary = "Consultar minha conta autenticada")
    @GetMapping("/me")
    public ContaAcessoResponse minhaConta(Authentication authentication) {
        ContaAcesso conta = service.buscarPorLogin(authentication.getName());
        return paraResponse(conta);
    }


    @Operation(summary = "Alterar minha senha")
    @PutMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarMinhaSenha(
            Authentication authentication,
            @Valid @RequestBody AlterarSenhaRequest request) {

        ContaAcesso conta = service.buscarPorLogin(authentication.getName());

        service.alterarSenha(
                conta.getId(),
                request.senhaAtual(),
                request.novaSenha()
        );
    }

}
