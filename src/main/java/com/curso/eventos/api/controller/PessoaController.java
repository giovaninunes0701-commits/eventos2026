package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.application.PessoaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
    name = "Pessoas",
    description = "Cadastro e consulta de pessoas."
)
@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {

    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar pessoa")
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(
            @Valid @RequestBody PessoaRequest request) {

        var pessoa = service.cadastrar(
            request.nome(),
            request.email(),
            request.cpf()
        );

        return ResponseEntity
            .created(URI.create("/api/pessoas/" + pessoa.getId()))
            .body(PessoaResponse.from(pessoa));
    }

    @Operation(summary = "Consultar pessoa por ID")
    @GetMapping("/{id}")
    public PessoaResponse buscar(@PathVariable Long id) {
        return PessoaResponse.from(service.buscarPorId(id));
    }

    @Operation(summary = "Listar pessoas")
    @GetMapping
    public List<PessoaResponse> listar() {
        return service.listar()
            .stream()
            .map(PessoaResponse::from)
            .toList();
    }
}
