package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaEventoTest {

    @Test
    void deveAdicionarEventoEManejarOsDoisLadosDaAssociacao() {
        CategoriaEvento categoria = new CategoriaEvento("Show");
        Evento evento = novoEvento("EVT-001");

        categoria.adicionarEvento(evento);

        assertEquals(1, categoria.getEventos().size());
        assertSame(evento, categoria.getEventos().getFirst());
        assertSame(categoria, evento.getCategoria());
    }

    @Test
    void naoDeveAdicionarEventoNulo() {
        CategoriaEvento categoria = new CategoriaEvento("Show");

        assertThrows(NullPointerException.class, () -> categoria.adicionarEvento(null));
    }

    @Test
    void naoDeveAdicionarDoisEventosComOMesmoCodigo() {
        CategoriaEvento categoria = new CategoriaEvento("Show");
        categoria.adicionarEvento(novoEvento("EVT-001"));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> categoria.adicionarEvento(novoEvento("EVT-001")));

        assertEquals("Código de evento já utilizado na categoria", excecao.getMessage());
    }

    @Test
    void naoDevePermitirQueEventoPertencaADuasCategorias() {
        CategoriaEvento show = new CategoriaEvento("Show");
        CategoriaEvento teatro = new CategoriaEvento("Teatro");
        Evento evento = novoEvento("EVT-001");
        show.adicionarEvento(evento);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> teatro.adicionarEvento(evento));

        assertEquals("Evento já pertence a outra categoria", excecao.getMessage());
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel() {
        CategoriaEvento categoria = new CategoriaEvento("Show");
        Evento evento = novoEvento("EVT-001");
        categoria.adicionarEvento(evento);

        assertThrows(
                UnsupportedOperationException.class,
                () -> categoria.getEventos().add(novoEvento("EVT-002")));
    }

    private Evento novoEvento(String codigo) {
        return new Evento(
                codigo,
                "Show de Rock",
                new BigDecimal("100"),
                new BigDecimal("50.00"),
                LocalDate.of(2026, 12, 20));
    }
}