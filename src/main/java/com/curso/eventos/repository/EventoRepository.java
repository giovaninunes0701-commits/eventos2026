package com.curso.eventos.repository;
import com.curso.eventos.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.*;
public interface EventoRepository extends JpaRepository<Evento,Long>, JpaSpecificationExecutor<Evento> {
    boolean existsByCodigo(String codigo);
    boolean existsByLocalId(Long localId);
    @Override @EntityGraph(attributePaths={"local","organizador"}) Optional<Evento> findById(Long id);
    @Override @EntityGraph(attributePaths={"local","organizador"}) List<Evento> findAll();
    @Override @EntityGraph(attributePaths={"local","organizador"})
    Page<Evento> findAll(Specification<Evento> specification, Pageable pageable);
}
