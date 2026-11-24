package com.curso.eventos.repository;

import com.curso.eventos.domain.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByPessoaId(Long pessoaId);
}
