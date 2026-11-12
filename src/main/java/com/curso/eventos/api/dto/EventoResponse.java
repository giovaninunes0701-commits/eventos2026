package com.curso.eventos.api.dto;
import com.curso.eventos.domain.Status;
import java.time.LocalDate;
public record EventoResponse(Long id, String codigo, String titulo, LocalDate dataEvento,
    Integer capacidade, Integer capacidadeMinima, Integer vagasDisponiveis, Status status,
    Long localId, String localNome, Long organizadorId, String organizadorNome) {}
