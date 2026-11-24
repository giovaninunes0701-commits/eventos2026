package com.curso.eventos.domain;

import com.curso.eventos.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersistenciaInscricaoTest {

    @Autowired
    PessoaRepository pessoas;

    @Autowired
    ParticipanteRepository participantes;

    @Autowired
    InscricaoRepository inscricoes;

    @Autowired
    EventoRepository eventos;

    @Test
    void deveSalvarPessoaParticipanteEInscricao() {
        Pessoa pessoa = pessoas.saveAndFlush(
            new Pessoa(
                "Participante Teste",
                "participante10@exemplo.com",
                "52998224725"
            )
        );

        assertNotNull(pessoa.getId());
        assertTrue(pessoas.existsByCpf("52998224725"));

        Participante participante = participantes.saveAndFlush(
            new Participante(pessoa, "17999999999")
        );

        assertNotNull(participante.getId());

        Evento evento = eventos.findAll()
            .stream()
            .findFirst()
            .orElseThrow();

        Inscricao inscricao = inscricoes.saveAndFlush(
            new Inscricao(participante, evento)
        );

        assertNotNull(inscricao.getId());

        assertTrue(
            inscricoes.existsByParticipanteIdAndEventoId(
                participante.getId(),
                evento.getId()
            )
        );

        assertEquals(
            StatusInscricao.SOLICITADA,
            inscricao.getStatus()
        );
    }
}
