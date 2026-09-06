package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventoTest {

    @Test
    void deveCriarEventoAtivoComDadosValidos() {
        Evento evento = novoEvento("100", "50.00");

        assertEquals("EVT-001", evento.getCodigo());
        assertEquals("Show de Rock", evento.getNome());
        assertEquals(Status.ATIVO, evento.getStatus());
        assertEquals(LocalDate.of(2026, 12, 20), evento.getDataEvento());
    }

    @Test
    void deveCalcularValorDaArrecadacaoPotencial() {
        Evento evento = novoEvento("100", "50.00");

        BigDecimal valor = evento.calcularValorArrecadacaoPotencial();

        assertEquals(0, new BigDecimal("5000.00").compareTo(valor));
    }

    @Test
    void deveVenderERepoIngressos() {
        Evento evento = novoEvento("100", "50.00");

        evento.venderIngressos(new BigDecimal("30"));
        evento.reporIngressos(new BigDecimal("10"));

        assertEquals(0, new BigDecimal("80").compareTo(evento.getIngressosDisponiveis()));
    }

    @Test
    void naoDeveVenderQuantidadeMaiorQueOsIngressosDisponiveis() {
        Evento evento = novoEvento("100", "50.00");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> evento.venderIngressos(new BigDecimal("101")));

        assertEquals("Ingressos disponíveis insuficientes", excecao.getMessage());
    }

    @Test
    void naoDeveCriarEventoComCodigoEmBranco() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Evento(
                        "  ",
                        "Show de Rock",
                        BigDecimal.ZERO,
                        new BigDecimal("50.00"),
                        LocalDate.of(2026, 12, 20)));
    }

    @Test
    void naoDeveCriarEventoComIngressosNegativos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoEvento("-1", "50.00"));
    }

    @Test
    void deveAlterarOStatusPorComportamentoExplicito() {
        Evento evento = novoEvento("100", "50.00");

        evento.inativar();
        assertEquals(Status.INATIVO, evento.getStatus());

        evento.ativar();
        assertEquals(Status.ATIVO, evento.getStatus());
    }

    private Evento novoEvento(String ingressos, String valorIngresso) {
        return new Evento(
                "EVT-001",
                "Show de Rock",
                new BigDecimal(ingressos),
                new BigDecimal(valorIngresso),
                LocalDate.of(2026, 12, 20));
    }
}