package com.curso.eventos.api.dto;

import jakarta.validation.constraints.*;

public record ParticipanteRequest(
    @NotNull @Positive Long pessoaId,
    @Size(max = 30) String telefone
) {}
