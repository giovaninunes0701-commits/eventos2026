package com.curso.eventos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContaAcessoRequest(
    @NotNull Long pessoaId,

    @NotBlank
    @Size(min = 3, max = 80)
    @Pattern(regexp = "^[A-Za-z0-9._-]+$")
    String login,

    @NotBlank
    @Size(min = 12, max = 72)
    String senha
) {
}
