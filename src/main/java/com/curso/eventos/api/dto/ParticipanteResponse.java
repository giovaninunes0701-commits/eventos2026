package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Participante;

public record ParticipanteResponse(
    Long id,
    Long pessoaId,
    String nome,
    String telefone
) {
    public static ParticipanteResponse from(Participante participante) {
        return new ParticipanteResponse(
            participante.getId(),
            participante.getPessoa().getId(),
            participante.getPessoa().getNome(),
            participante.getTelefone()
        );
    }
}
