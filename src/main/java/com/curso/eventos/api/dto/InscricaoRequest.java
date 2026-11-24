package com.curso.eventos.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InscricaoRequest(
    @NotNull @Positive Long participanteId,
    @NotNull @Positive Long eventoId
) {}
