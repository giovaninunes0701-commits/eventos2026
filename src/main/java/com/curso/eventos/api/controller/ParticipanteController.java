package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.application.ParticipanteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
    name = "Participantes",
    description = "Cadastro e consulta de participantes."
)
@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService service;

    public ParticipanteController(ParticipanteService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar participante")
    @PostMapping
    public ResponseEntity<ParticipanteResponse> cadastrar(
            @Valid @RequestBody ParticipanteRequest request) {

        var participante = service.cadastrar(
            request.pessoaId(),
            request.telefone()
        );

        return ResponseEntity
            .created(URI.create("/api/participantes/" + participante.getId()))
            .body(ParticipanteResponse.from(participante));
    }

    @Operation(summary = "Consultar participante por ID")
    @GetMapping("/{id}")
    public ParticipanteResponse buscar(@PathVariable Long id) {
        return ParticipanteResponse.from(service.buscarPorId(id));
    }

    @Operation(summary = "Listar participantes")
    @GetMapping
    public List<ParticipanteResponse> listar() {
        return service.listar()
            .stream()
            .map(ParticipanteResponse::from)
            .toList();
    }
}
