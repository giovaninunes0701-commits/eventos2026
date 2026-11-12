package com.curso.eventos.api.dto;
import jakarta.validation.constraints.*;
public record OrganizadorRequest(@NotBlank @Size(max=120) String nome, @NotBlank @Email @Size(max=150) String email) {}
