package com.curso.eventos.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.curso.eventos.api.dto.*;
import com.curso.eventos.api.mapper.LocalEventoMapper;
import com.curso.eventos.application.LocalEventoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@Tag(name = "Locais", description = "Gerenciamento dos locais dos eventos.")
@RestController @RequestMapping("/api/locais")
public class LocalEventoController {
    private final LocalEventoService service; private final LocalEventoMapper mapper;
    public LocalEventoController(LocalEventoService service, LocalEventoMapper mapper) { this.service=service; this.mapper=mapper; }
    @Operation(summary = "Cadastrar local")
    @ApiResponse(responseCode = "201", description = "Operacao realizada")
    @PostMapping public ResponseEntity<LocalEventoResponse> cadastrar(@Valid @RequestBody LocalEventoRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request));
        return ResponseEntity.created(URI.create("/api/locais/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
    @Operation(summary = "Consultar local por ID")
    @ApiResponse(responseCode = "200", description = "Operacao realizada")
    @GetMapping("/{id}") public LocalEventoResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @Operation(summary = "Listar locais")
    @ApiResponse(responseCode = "200", description = "Operacao realizada")
    @GetMapping public List<LocalEventoResponse> listar() { return service.listar().stream().map(mapper::toResponse).toList(); }
    @Operation(summary = "Atualizar local")
    @ApiResponse(responseCode = "200", description = "Operacao realizada")
    @PutMapping("/{id}") public LocalEventoResponse alterar(@PathVariable Long id,
            @Valid @RequestBody LocalEventoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome(), request.endereco()));
    }
    @Operation(summary = "Excluir local")
    @ApiResponse(responseCode = "204", description = "Operacao realizada")
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
