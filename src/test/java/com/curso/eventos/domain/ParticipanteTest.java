package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipanteTest {

    @Test
    void deveAssociarParticipanteAPessoa() {
        Pessoa pessoa = new Pessoa(
            "Ana",
            "ana@exemplo.com",
            "52998224725"
        );

        Participante participante = new Participante(
            pessoa,
            "17999999999"
        );

        assertSame(pessoa, participante.getPessoa());
        assertEquals("Ana", participante.getPessoa().getNome());
        assertEquals("17999999999", participante.getTelefone());
    }

    @Test
    void deveRejeitarParticipanteSemPessoa() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Participante(null, "17999999999")
        );
    }
}
