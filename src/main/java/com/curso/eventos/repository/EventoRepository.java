package com.curso.eventos.repository;

import com.curso.eventos.domain.Evento;
import com.curso.eventos.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    Optional<Evento> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Evento> findByCategoriaId(Long categoriaId);

    List<Evento> findByStatus(Status status);
}
