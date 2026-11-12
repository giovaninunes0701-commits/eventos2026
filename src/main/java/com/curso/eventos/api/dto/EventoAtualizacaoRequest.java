package com.curso.eventos.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EventoAtualizacaoRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotNull LocalDate dataEvento,
        @NotNull @PositiveOrZero Integer capacidade,
        @NotNull @PositiveOrZero Integer capacidadeMinima,
        @NotNull @Positive Long localId,
        @Positive Long organizadorId) {
    @AssertTrue(message = "Capacidade mínima deve ser menor ou igual à capacidade")
    public boolean isCapacidadeCoerente() {
        return capacidade == null || capacidadeMinima == null || capacidadeMinima <= capacidade;
    }
}
