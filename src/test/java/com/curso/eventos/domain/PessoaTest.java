package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    @Test
    void deveCriarPessoaComDadosValidos() {
        Pessoa pessoa = new Pessoa(
            "Giovani",
            "GIOVANI@EXEMPLO.COM",
            "52998224725"
        );

        assertEquals("Giovani", pessoa.getNome());
        assertEquals("giovani@exemplo.com", pessoa.getEmail());
        assertEquals("52998224725", pessoa.getCpf());
    }

    @Test
    void deveRejeitarCpfInvalido() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pessoa(
                "Giovani",
                "giovani@exemplo.com",
                "11111111111"
            )
        );
    }
}
