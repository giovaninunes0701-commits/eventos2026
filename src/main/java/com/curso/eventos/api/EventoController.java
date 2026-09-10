package com.curso.eventos.api;

import com.curso.eventos.api.dto.EventoRequest;
import com.curso.eventos.api.dto.EventoResponse;
import com.curso.eventos.api.mapper.EventoMapper;
import com.curso.eventos.application.EventoService;
import com.curso.eventos.domain.Evento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;
    private final EventoMapper mapper;

    public EventoController(EventoService service, EventoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> cadastrar(
            @Valid @RequestBody EventoRequest request) {

        Evento evento = mapper.toEntity(request);
        Evento cadastrado = service.cadastrar(
                evento,
                request.categoriaId(),
                request.organizadorId());
        URI location = URI.create("/api/eventos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public EventoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Transactional(readOnly = true)
    @GetMapping
    public List<EventoResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
