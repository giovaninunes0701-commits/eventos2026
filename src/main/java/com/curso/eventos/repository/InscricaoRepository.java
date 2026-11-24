package com.curso.eventos.repository;

import com.curso.eventos.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteIdAndEventoId(
        Long participanteId,
        Long eventoId
    );
}
