package com.curso.eventos.repository;

import com.curso.eventos.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
