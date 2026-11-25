package com.curso.eventos.application;

import com.curso.eventos.domain.*;
import com.curso.eventos.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricoes;
    private final ParticipanteRepository participantes;
    private final EventoRepository eventos;
    private final EntityManager entityManager;

    public InscricaoService(
            InscricaoRepository inscricoes,
            ParticipanteRepository participantes,
            EventoRepository eventos,
            EntityManager entityManager) {

        this.inscricoes = inscricoes;
        this.participantes = participantes;
        this.eventos = eventos;
        this.entityManager = entityManager;
    }

    @Transactional
    public Inscricao cadastrar(Long participanteId, Long eventoId) {

        Participante participante = participantes.findById(participanteId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Participante não encontrado"
            ));

        Evento evento = eventos.findById(eventoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Evento não encontrado"
            ));

        if (inscricoes.existsByParticipanteIdAndEventoId(
                participanteId, eventoId)) {

            throw new RecursoDuplicadoException(
                "Participante já inscrito neste evento"
            );
        }

        return inscricoes.save(
            new Inscricao(participante, evento)
        );
    }

    @Transactional(readOnly = true)
    public Inscricao buscarPorId(Long id) {

        return inscricoes.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Inscrição não encontrada"
            ));
    }

    @Transactional
    public Inscricao confirmar(Long id) {

        Inscricao inscricao = buscarPorId(id);

        entityManager.flush();
        entityManager.refresh(
            inscricao,
            LockModeType.PESSIMISTIC_WRITE
        );

        entityManager.refresh(
            inscricao.getEvento(),
            LockModeType.PESSIMISTIC_WRITE
        );

        inscricao.confirmar();

        return inscricao;
    }

    @Transactional
    public Inscricao cancelar(Long id) {

        Inscricao inscricao = buscarPorId(id);

        entityManager.flush();
        entityManager.refresh(
            inscricao,
            LockModeType.PESSIMISTIC_WRITE
        );

        if (inscricao.getStatus() == StatusInscricao.CONFIRMADA) {
            entityManager.flush();

            entityManager.refresh(
                inscricao.getEvento(),
                LockModeType.PESSIMISTIC_WRITE
            );
        }

        inscricao.cancelar();

        return inscricao;
    }
}
