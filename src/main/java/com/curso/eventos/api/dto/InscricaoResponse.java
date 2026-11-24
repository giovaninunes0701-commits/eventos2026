package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Inscricao;
import com.curso.eventos.domain.StatusInscricao;

public record InscricaoResponse(
    Long id,
    Long participanteId,
    Long eventoId,
    StatusInscricao status
) {
    public static InscricaoResponse from(Inscricao inscricao) {
        return new InscricaoResponse(
            inscricao.getId(),
            inscricao.getParticipante().getId(),
            inscricao.getEvento().getId(),
            inscricao.getStatus()
        );
    }
}
