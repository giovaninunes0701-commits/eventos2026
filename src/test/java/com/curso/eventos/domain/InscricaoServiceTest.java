package com.curso.eventos.domain;

import com.curso.eventos.application.*;
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
class InscricaoServiceTest {

    @Autowired PessoaService pessoaService;
    @Autowired ParticipanteService participanteService;
    @Autowired InscricaoService inscricaoService;
    @Autowired EventoRepository eventos;
    @Autowired jakarta.persistence.EntityManager entityManager;

    private Inscricao novaInscricao() {

        Pessoa pessoa = pessoaService.cadastrar(
            "Aluno Aula 10",
            "aluno10@exemplo.com",
            "52998224725"
        );

        Participante participante = participanteService.cadastrar(
            pessoa.getId(),
            "17999999999"
        );

        Evento evento = eventos.findAll()
            .stream()
            .filter(e -> e.getStatus() == Status.ATIVO)
            .findFirst()
            .orElseThrow();

        return inscricaoService.cadastrar(
            participante.getId(),
            evento.getId()
        );
    }

    @Test
    void deveCadastrarInscricaoSolicitada() {

        Inscricao inscricao = novaInscricao();

        assertNotNull(inscricao.getId());

        assertEquals(
            StatusInscricao.SOLICITADA,
            inscricao.getStatus()
        );
    }

    @Test
    void deveConfirmarEReservarUmaVaga() {

        Inscricao inscricao = novaInscricao();

        int vagasAntes =
            inscricao.getEvento().getVagasDisponiveis();

        inscricaoService.confirmar(inscricao.getId());

        assertEquals(
            StatusInscricao.CONFIRMADA,
            inscricao.getStatus()
        );

        assertEquals(
            vagasAntes - 1,
            inscricao.getEvento().getVagasDisponiveis()
        );
    }

    @Test
    void deveCancelarEDevolverVaga() {

        Inscricao inscricao = novaInscricao();

        int vagasAntes =
            inscricao.getEvento().getVagasDisponiveis();

        inscricaoService.confirmar(inscricao.getId());
        inscricaoService.cancelar(inscricao.getId());

        assertEquals(
            StatusInscricao.CANCELADA,
            inscricao.getStatus()
        );

        assertEquals(
            vagasAntes,
            inscricao.getEvento().getVagasDisponiveis()
        );
    }

    @Test
    void devePersistirConfirmacaoECancelamentoNoBanco() {
        Inscricao inscricao = novaInscricao();

        Long inscricaoId = inscricao.getId();
        Long eventoId = inscricao.getEvento().getId();

        int vagasIniciais =
            inscricao.getEvento().getVagasDisponiveis();

        inscricaoService.confirmar(inscricaoId);

        entityManager.flush();
        entityManager.clear();

        Inscricao confirmada =
            inscricaoService.buscarPorId(inscricaoId);

        Evento eventoConfirmado =
            eventos.findById(eventoId).orElseThrow();

        assertEquals(
            StatusInscricao.CONFIRMADA,
            confirmada.getStatus()
        );

        assertEquals(
            vagasIniciais - 1,
            eventoConfirmado.getVagasDisponiveis()
        );

        inscricaoService.cancelar(inscricaoId);

        entityManager.flush();
        entityManager.clear();

        Inscricao cancelada =
            inscricaoService.buscarPorId(inscricaoId);

        Evento eventoCancelado =
            eventos.findById(eventoId).orElseThrow();

        assertEquals(
            StatusInscricao.CANCELADA,
            cancelada.getStatus()
        );

        assertEquals(
            vagasIniciais,
            eventoCancelado.getVagasDisponiveis()
        );
    }
}
