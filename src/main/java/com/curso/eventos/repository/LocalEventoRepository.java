package com.curso.eventos.repository;
import com.curso.eventos.domain.LocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LocalEventoRepository extends JpaRepository<LocalEvento,Long> { boolean existsByNome(String nome); }
