package com.curso.eventos.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("testpg")
class PostgresConexaoTest {

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void deveConectarAoBancoCorreto() {
        String banco = jdbc.queryForObject(
            "SELECT current_database()", String.class
        );

        assertEquals("eventos2026_test", banco);
    }

    @Test
    void deveTerTabelasDaAula10() {
        Integer quantidade = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM information_schema.tables
            WHERE table_schema = 'public'
              AND table_name IN ('pessoa', 'participante', 'inscricao', 'evento')
            """, Integer.class
        );

        assertEquals(4, quantidade);
    }
}
