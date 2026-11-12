package com.curso.eventos.api.controller;

import com.curso.eventos.api.dto.*;
import com.curso.eventos.api.mapper.EventoMapper;
import com.curso.eventos.application.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.time.LocalDate;
import com.curso.eventos.domain.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController @RequestMapping("/api/eventos")
public class EventoController {
    private final EventoService service; private final EventoMapper mapper;
    public EventoController(EventoService service, EventoMapper mapper) { this.service=service; this.mapper=mapper; }
    @PostMapping public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request),request.localId(),request.organizadorId());
        return ResponseEntity.created(URI.create("/api/eventos/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
    @GetMapping("/{id}") public EventoResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @GetMapping public PaginaResponse<EventoResponse> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long localId,
            @RequestParam(required = false) Long organizadorId,
            @RequestParam(required = false) LocalDate dataInicial,
            @RequestParam(required = false) LocalDate dataFinal,
            @RequestParam(required = false) Boolean vagasDisponiveis,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return PaginaResponse.de(service.pesquisar(titulo, status, localId, organizadorId,
                dataInicial, dataFinal, vagasDisponiveis, pageable), mapper::toResponse);
    }
    @PutMapping("/{id}") public EventoResponse alterar(@PathVariable Long id,
            @Valid @RequestBody EventoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.titulo(), request.dataEvento(),
                request.capacidade(), request.capacidadeMinima(), request.localId(), request.organizadorId()));
    }
    @PutMapping("/{id}/status") public EventoResponse alterarStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }
    @PostMapping("/{id}/vagas/reservas") public EventoResponse reservarVagas(@PathVariable Long id,
            @Valid @RequestBody MovimentacaoVagasRequest request) {
        return mapper.toResponse(service.reservarVagas(id, request.quantidade()));
    }
    @PostMapping("/{id}/vagas/liberacoes") public EventoResponse liberarVagas(@PathVariable Long id,
            @Valid @RequestBody MovimentacaoVagasRequest request) {
        return mapper.toResponse(service.liberarVagas(id, request.quantidade()));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
