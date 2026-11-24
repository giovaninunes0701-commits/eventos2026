package com.curso.eventos.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Eventos", description = "Cadastro, consulta, atualização, exclusão e controle de vagas.")
@RestController @RequestMapping("/api/eventos")
public class EventoController {
    private final EventoService service; private final EventoMapper mapper;
    public EventoController(EventoService service, EventoMapper mapper) { this.service=service; this.mapper=mapper; }
        @Operation(summary = "Cadastrar novo evento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evento cadastrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Local ou organizador não encontrado"),
        @ApiResponse(responseCode = "409", description = "Código já cadastrado")
    })
    @PostMapping public ResponseEntity<EventoResponse> cadastrar(@Valid @RequestBody EventoRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request),request.localId(),request.organizadorId());
        return ResponseEntity.created(URI.create("/api/eventos/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
        @Operation(summary = "Consultar evento por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/{id}") public EventoResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
        @Operation(summary = "Listar eventos com filtros e paginação")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta realizada"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
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
    @Operation(summary = "Atualizar dados do evento")
    @PutMapping("/{id}") public EventoResponse alterar(@PathVariable Long id,
            @Valid @RequestBody EventoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.titulo(), request.dataEvento(),
                request.capacidade(), request.capacidadeMinima(), request.localId(), request.organizadorId()));
    }
    @Operation(summary = "Ativar ou inativar evento")
    @PutMapping("/{id}/status") public EventoResponse alterarStatus(@PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }
    @Operation(summary = "Reservar vagas de um evento")
    @PostMapping("/{id}/vagas/reservas") public EventoResponse reservarVagas(@PathVariable Long id,
            @Valid @RequestBody MovimentacaoVagasRequest request) {
        return mapper.toResponse(service.reservarVagas(id, request.quantidade()));
    }
    @Operation(summary = "Liberar vagas de um evento")
    @PostMapping("/{id}/vagas/liberacoes") public EventoResponse liberarVagas(@PathVariable Long id,
            @Valid @RequestBody MovimentacaoVagasRequest request) {
        return mapper.toResponse(service.liberarVagas(id, request.quantidade()));
    }
        @Operation(summary = "Excluir evento")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Evento excluído"),
        @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
