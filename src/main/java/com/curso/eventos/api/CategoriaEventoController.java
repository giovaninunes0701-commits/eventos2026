package com.curso.eventos.api;

import com.curso.eventos.api.dto.CategoriaEventoRequest;
import com.curso.eventos.api.dto.CategoriaEventoResponse;
import com.curso.eventos.api.mapper.CategoriaEventoMapper;
import com.curso.eventos.application.CategoriaEventoService;
import com.curso.eventos.domain.CategoriaEvento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias-eventos")
public class CategoriaEventoController {

    private final CategoriaEventoService service;
    private final CategoriaEventoMapper mapper;

    public CategoriaEventoController(CategoriaEventoService service, CategoriaEventoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CategoriaEventoResponse> cadastrar(
            @Valid @RequestBody CategoriaEventoRequest request) {
        CategoriaEvento categoria = service.cadastrar(request.nome());
        URI location = URI.create("/api/categorias-eventos/" + categoria.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(categoria));
    }

    @GetMapping("/{id}")
    public CategoriaEventoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<CategoriaEventoResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
