package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InscricaoTest {

    private Inscricao novaInscricao() {
        Pessoa pessoa = new Pessoa(
            "Ana",
            "ana@exemplo.com",
            "52998224725"
        );

        Participante participante = new Participante(
            pessoa, "17999999999"
        );

        Evento evento = new Evento(
            "A10-001",
            "Feira de Tecnologia",
            LocalDate.of(2026, 12, 10),
            100,
            10
        );

        return new Inscricao(participante, evento);
    }

    @Test
    void deveCriarInscricaoSolicitada() {
        Inscricao inscricao = novaInscricao();

        assertEquals(
            StatusInscricao.SOLICITADA,
            inscricao.getStatus()
        );
    }

    @Test
    void deveConfirmarInscricao() {
        Inscricao inscricao = novaInscricao();

        inscricao.confirmar();

        assertEquals(
            StatusInscricao.CONFIRMADA,
            inscricao.getStatus()
        );
    }

    @Test
    void deveImpedirConfirmacaoAposCancelamento() {
        Inscricao inscricao = novaInscricao();

        inscricao.cancelar();

        assertThrows(
            IllegalStateException.class,
            inscricao::confirmar
        );
    }

    @Test
    void deveReservarVagaAoConfirmar() {
        Inscricao inscricao = novaInscricao();
        Evento evento = inscricao.getEvento();

        assertEquals(100, evento.getVagasDisponiveis());

        inscricao.confirmar();

        assertEquals(99, evento.getVagasDisponiveis());
    }

    @Test
    void deveLiberarVagaAoCancelarConfirmada() {
        Inscricao inscricao = novaInscricao();
        Evento evento = inscricao.getEvento();

        inscricao.confirmar();
        inscricao.cancelar();

        assertEquals(100, evento.getVagasDisponiveis());
        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
    }

    @Test
    void naoDeveAlterarVagasAoCancelarSolicitada() {
        Inscricao inscricao = novaInscricao();
        Evento evento = inscricao.getEvento();

        inscricao.cancelar();

        assertEquals(100, evento.getVagasDisponiveis());
    }
}
