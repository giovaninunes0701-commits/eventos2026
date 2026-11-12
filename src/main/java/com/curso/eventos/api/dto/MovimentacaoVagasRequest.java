package com.curso.eventos.api.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoVagasRequest(@NotNull @Positive Integer quantidade) {}
