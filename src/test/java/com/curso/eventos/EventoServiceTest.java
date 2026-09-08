package com.curso.eventos;

import com.curso.eventos.application.EventoService;
import com.curso.eventos.application.RecursoNaoEncontradoException;
import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.domain.Evento;
import com.curso.eventos.repository.CategoriaEventoRepository;
import com.curso.eventos.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventoServiceTest {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CategoriaEventoRepository categoriaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    void deveCadastrarEventoComCategoria() {
        CategoriaEvento categoria = categoriaRepository.save(
                new CategoriaEvento("Show"));

        Evento cadastrado = eventoService.cadastrar(
                novoEvento("SRV-001"),
                categoria.getId(),
                null);

        assertNotNull(cadastrado.getId());
        assertEquals(categoria.getId(), cadastrado.getCategoria().getId());
    }

    @Test
    void naoDeveCadastrarComCategoriaInexistenteEDeveFazerRollback() {
        Evento evento = novoEvento("SRV-002");

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> eventoService.cadastrar(evento, Long.MAX_VALUE, null));

        assertFalse(eventoRepository.existsByCodigo("SRV-002"));
    }

    private Evento novoEvento(String codigo) {
        return new Evento(
                codigo,
                "Evento de teste",
                new BigDecimal("100"),
                new BigDecimal("50.00"),
                LocalDate.of(2026, 12, 1));
    }
}
