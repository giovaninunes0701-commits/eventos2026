package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Status;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull Status status) {}
