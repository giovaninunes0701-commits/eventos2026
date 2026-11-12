package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EventoTest {
    private Evento novoEvento() {
        return new Evento("TEST-1", "Feira", LocalDate.of(2026, 11, 20), 10, 2);
    }
    @Test void reservarELiberarVagas() {
        Evento e = novoEvento();
        e.reservarVagas(3);
        assertEquals(7, e.getVagasDisponiveis());
        e.liberarVagas(2);
        assertEquals(9, e.getVagasDisponiveis());
    }
    @Test void naoPermitirReservaAcimaDaCapacidade() {
        Evento e = novoEvento();
        assertThrows(IllegalArgumentException.class, () -> e.reservarVagas(11));
        assertThrows(IllegalArgumentException.class, () -> e.reservarVagas(0));
    }
    @Test void naoPermitirReservaEmEventoInativo() {
        Evento e = novoEvento();
        e.alterarStatus(Status.INATIVO);
        assertThrows(IllegalArgumentException.class, () -> e.reservarVagas(1));
    }
    @Test void alterarCapacidadePreservaVagasOcupadas() {
        Evento e = novoEvento();
        e.reservarVagas(4);
        e.alterarDados("Feira atualizada", LocalDate.of(2026, 11, 21), 12, 2);
        assertEquals(8, e.getVagasDisponiveis());
        assertThrows(IllegalArgumentException.class,
                () -> e.alterarDados("Feira", LocalDate.of(2026, 11, 22), 3, 2));
    }
}
