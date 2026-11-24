package com.curso.eventos.api.dto;

import com.curso.eventos.api.validation.CpfValido;
import jakarta.validation.constraints.*;

public record PessoaRequest(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @CpfValido String cpf
) {}
