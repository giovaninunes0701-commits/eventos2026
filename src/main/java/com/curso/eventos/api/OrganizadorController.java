package com.curso.eventos.api;

import com.curso.eventos.api.dto.OrganizadorRequest;
import com.curso.eventos.api.dto.OrganizadorResponse;
import com.curso.eventos.api.mapper.OrganizadorMapper;
import com.curso.eventos.application.OrganizadorService;
import com.curso.eventos.domain.Organizador;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/organizadores")
public class OrganizadorController {

    private final OrganizadorService service;
    private final OrganizadorMapper mapper;

    public OrganizadorController(OrganizadorService service, OrganizadorMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<OrganizadorResponse> cadastrar(
            @Valid @RequestBody OrganizadorRequest request) {
        Organizador organizador = service.cadastrar(mapper.toEntity(request));
        URI location = URI.create("/api/organizadores/" + organizador.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(organizador));
    }

    @GetMapping("/{id}")
    public OrganizadorResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<OrganizadorResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
