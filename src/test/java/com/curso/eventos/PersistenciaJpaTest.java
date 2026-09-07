package com.curso.eventos;

import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.domain.Evento;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class PersistenciaJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void devePersistirERelerCategoriaEEvento() {
        CategoriaEvento categoria = new CategoriaEvento("Show de Rock");
        Evento evento = new Evento(
                "EVT-100",
                "Rock in Rio Tributo",
                new BigDecimal("500"),
                new BigDecimal("120.00"),
                LocalDate.of(2026, 11, 15));

        categoria.adicionarEvento(evento);
        entityManager.persist(categoria);
        entityManager.persist(evento);
        entityManager.flush();

        Long eventoId = evento.getId();
        entityManager.clear();

        Evento recuperado = entityManager.find(Evento.class, eventoId);
        assertEquals("Show de Rock", recuperado.getCategoria().getNome());
    }

    @Test
    @Transactional
    void naoDevePermitirCodigoDuplicadoNoBanco() {
        CategoriaEvento categoria = new CategoriaEvento("Teatro");
        entityManager.persist(categoria);

        Evento evento1 = new Evento(
                "EVT-200",
                "Peça Teatral A",
                new BigDecimal("100"),
                new BigDecimal("50.00"),
                LocalDate.of(2026, 10, 1));
        categoria.adicionarEvento(evento1);
        entityManager.persist(evento1);
        entityManager.flush();

        assertThrows(Exception.class, () -> {
            CategoriaEvento outraCategoria = new CategoriaEvento("Teatro 2");
            entityManager.persist(outraCategoria);

            Evento evento2 = new Evento(
                    "EVT-200",
                    "Peça Teatral B",
                    new BigDecimal("100"),
                    new BigDecimal("60.00"),
                    LocalDate.of(2026, 10, 2));
            outraCategoria.adicionarEvento(evento2);

            entityManager.persist(evento2);
            entityManager.flush();
        });
    }
}