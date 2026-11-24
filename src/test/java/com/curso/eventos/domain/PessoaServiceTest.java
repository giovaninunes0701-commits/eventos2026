package com.curso.eventos.domain;

import com.curso.eventos.application.PessoaService;
import com.curso.eventos.application.RecursoDuplicadoException;
import com.curso.eventos.repository.PessoaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PessoaServiceTest {

    @Autowired
    PessoaService service;

    @Autowired
    PessoaRepository repository;

    @Test
    void deveCadastrarPessoa() {
        Pessoa pessoa = service.cadastrar(
            "Maria",
            "MARIA@EXEMPLO.COM",
            "52998224725"
        );

        assertNotNull(pessoa.getId());
        assertEquals(
            "maria@exemplo.com",
            pessoa.getEmail()
        );
    }

    @Test
    void deveImpedirCpfDuplicado() {
        service.cadastrar(
            "Maria",
            "maria@exemplo.com",
            "52998224725"
        );

        assertThrows(
            RecursoDuplicadoException.class,
            () -> service.cadastrar(
                "Outra Pessoa",
                "outra@exemplo.com",
                "52998224725"
            )
        );
    }

    @Test
    void deveImpedirEmailDuplicado() {
        service.cadastrar(
            "Maria",
            "maria@exemplo.com",
            "52998224725"
        );

        assertThrows(
            RecursoDuplicadoException.class,
            () -> service.cadastrar(
                "Outra Pessoa",
                "MARIA@EXEMPLO.COM",
                "11144477735"
            )
        );
    }
}
