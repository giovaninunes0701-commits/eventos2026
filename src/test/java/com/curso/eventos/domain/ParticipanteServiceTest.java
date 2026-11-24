package com.curso.eventos.domain;

import com.curso.eventos.application.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParticipanteServiceTest {

    @Autowired
    PessoaService pessoaService;

    @Autowired
    ParticipanteService participanteService;

    private Pessoa novaPessoa() {
        return pessoaService.cadastrar(
            "Carlos",
            "carlos.aula10@exemplo.com",
            "52998224725"
        );
    }

    @Test
    void deveCadastrarParticipante() {
        Pessoa pessoa = novaPessoa();

        Participante participante = participanteService.cadastrar(
            pessoa.getId(),
            "17999999999"
        );

        assertNotNull(participante.getId());
        assertEquals(pessoa.getId(), participante.getPessoa().getId());
    }

    @Test
    void deveImpedirParticipanteDuplicado() {
        Pessoa pessoa = novaPessoa();

        participanteService.cadastrar(
            pessoa.getId(),
            "17999999999"
        );

        assertThrows(
            RecursoDuplicadoException.class,
            () -> participanteService.cadastrar(
                pessoa.getId(),
                "17888888888"
            )
        );
    }

    @Test
    void deveRejeitarPessoaInexistente() {
        assertThrows(
            RecursoNaoEncontradoException.class,
            () -> participanteService.cadastrar(
                999999L,
                "17999999999"
            )
        );
    }
}
