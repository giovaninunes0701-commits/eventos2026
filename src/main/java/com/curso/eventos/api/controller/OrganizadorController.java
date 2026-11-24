package com.curso.eventos.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.curso.eventos.api.dto.*;
import com.curso.eventos.api.mapper.OrganizadorMapper;
import com.curso.eventos.application.OrganizadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@Tag(name = "Organizadores", description = "Cadastro e consulta de organizadores.")
@RestController @RequestMapping("/api/organizadores")
public class OrganizadorController {
    private final OrganizadorService service; private final OrganizadorMapper mapper;
    public OrganizadorController(OrganizadorService service, OrganizadorMapper mapper) { this.service=service; this.mapper=mapper; }
    @Operation(summary = "Cadastrar organizador")
    @ApiResponse(responseCode = "201", description = "Operacao realizada")
    @PostMapping public ResponseEntity<OrganizadorResponse> cadastrar(@Valid @RequestBody OrganizadorRequest request) {
        var salvo=service.cadastrar(mapper.toEntity(request));
        return ResponseEntity.created(URI.create("/api/organizadores/"+salvo.getId())).body(mapper.toResponse(salvo));
    }
    @Operation(summary = "Consultar organizador por ID")
    @ApiResponse(responseCode = "200", description = "Operacao realizada")
    @GetMapping("/{id}") public OrganizadorResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @Operation(summary = "Listar organizadores")
    @ApiResponse(responseCode = "200", description = "Operacao realizada")
    @GetMapping public List<OrganizadorResponse> listar() { return service.listar().stream().map(mapper::toResponse).toList(); }
}
