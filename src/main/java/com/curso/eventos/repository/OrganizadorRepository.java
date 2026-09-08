package com.curso.eventos.repository;

import com.curso.eventos.domain.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {
    boolean existsByCnpj(String cnpj);
}