package com.curso.eventos.api.dto;
import jakarta.validation.constraints.*;
public record LocalEventoRequest(@NotBlank @Size(max=120) String nome, @NotBlank @Size(max=200) String endereco) {}
