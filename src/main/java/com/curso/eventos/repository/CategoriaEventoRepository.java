package com.curso.eventos.repository;

import com.curso.eventos.domain.CategoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaEventoRepository
        extends JpaRepository<CategoriaEvento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<CategoriaEvento> findByNomeIgnoreCase(String nome);
}