package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.api.mapper.OrganizadorMapper;
import com.curso.eventos.application.OrganizadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/organizadores")
public class OrganizadorController {
    private final OrganizadorService service; private final OrganizadorMapper mapper;
    public OrganizadorController(OrganizadorService service, OrganizadorMapper mapper) { this.service=service; this.mapper=mapper; }
    @PostMapping public ResponseEntity<OrganizadorResponse> cadastrar(@Valid @RequestBody OrganizadorRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request));
        return ResponseEntity.created(URI.create("/api/organizadores/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
    @GetMapping("/{id}") public OrganizadorResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @GetMapping public List<OrganizadorResponse> listar() { return service.listar().stream().map(mapper::toResponse).toList(); }
}
