package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Status;

public record CategoriaEventoResponse(
        Long id,
        String nome,
        Status status
) {
}
