package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.api.mapper.LocalEventoMapper;
import com.curso.eventos.application.LocalEventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/locais")
public class LocalEventoController {
    private final LocalEventoService service; private final LocalEventoMapper mapper;
    public LocalEventoController(LocalEventoService service, LocalEventoMapper mapper) { this.service=service; this.mapper=mapper; }
    @PostMapping public ResponseEntity<LocalEventoResponse> cadastrar(@Valid @RequestBody LocalEventoRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request));
        return ResponseEntity.created(URI.create("/api/locais/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
    @GetMapping("/{id}") public LocalEventoResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @GetMapping public List<LocalEventoResponse> listar() { return service.listar().stream().map(mapper::toResponse).toList(); }
    @PutMapping("/{id}") public LocalEventoResponse alterar(@PathVariable Long id,
            @Valid @RequestBody LocalEventoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome(), request.endereco()));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
