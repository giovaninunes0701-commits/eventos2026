package com.curso.eventos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventoRequest(
        @NotBlank(message = "Código do evento é obrigatório")
        @Size(max = 50, message = "Código deve possuir no máximo 50 caracteres")
        String codigo,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve possuir no máximo 150 caracteres")
        String nome,

        @NotNull(message = "Ingressos disponíveis é obrigatório")
        @PositiveOrZero(message = "Ingressos disponíveis não pode ser negativo")
        BigDecimal ingressosDisponiveis,

        @NotNull(message = "Valor do ingresso é obrigatório")
        @PositiveOrZero(message = "Valor do ingresso não pode ser negativo")
        BigDecimal valorIngresso,

        @NotNull(message = "Ingressos mínimos de alerta é obrigatório")
        @PositiveOrZero(message = "Ingressos mínimos de alerta não pode ser negativo")
        BigDecimal ingressosMinimosAlerta,

        @NotNull(message = "Data do evento é obrigatória")
        LocalDate dataEvento,

        @NotNull(message = "Categoria é obrigatória")
        @Positive(message = "Identificador da categoria deve ser positivo")
        Long categoriaId,

        @Positive(message = "Identificador do organizador deve ser positivo")
        Long organizadorId
) {
}
