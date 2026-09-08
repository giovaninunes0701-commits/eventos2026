package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventoResponse(
        Long id,
        String codigo,
        String nome,
        BigDecimal ingressosDisponiveis,
        BigDecimal valorIngresso,
        BigDecimal ingressosMinimosAlerta,
        BigDecimal valorArrecadacaoPotencial,
        LocalDate dataEvento,
        Status status,
        Long categoriaId,
        String categoriaNome,
        Long organizadorId,
        String organizadorRazaoSocial
) {
}
