package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.application.InscricaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(
    name = "Inscricoes",
    description = "Cadastro, confirmacao e cancelamento de inscricoes."
)
@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    private final InscricaoService service;

    public InscricaoController(InscricaoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar inscricao")
    @PostMapping
    public ResponseEntity<InscricaoResponse> cadastrar(
            @Valid @RequestBody InscricaoRequest request) {

        var inscricao = service.cadastrar(
            request.participanteId(),
            request.eventoId()
        );

        return ResponseEntity
            .created(URI.create("/api/inscricoes/" + inscricao.getId()))
            .body(InscricaoResponse.from(inscricao));
    }

    @Operation(summary = "Consultar inscricao por ID")
    @GetMapping("/{id}")
    public InscricaoResponse buscar(@PathVariable Long id) {
        return InscricaoResponse.from(service.buscarPorId(id));
    }

    @Operation(summary = "Confirmar inscricao e reservar vaga")
    @PutMapping("/{id}/confirmacao")
    public InscricaoResponse confirmar(@PathVariable Long id) {
        return InscricaoResponse.from(service.confirmar(id));
    }

    @Operation(summary = "Cancelar inscricao e liberar vaga")
    @PutMapping("/{id}/cancelamento")
    public InscricaoResponse cancelar(@PathVariable Long id) {
        return InscricaoResponse.from(service.cancelar(id));
    }
}
