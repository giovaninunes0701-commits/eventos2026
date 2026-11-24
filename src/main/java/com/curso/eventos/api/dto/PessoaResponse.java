package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Pessoa;

public record PessoaResponse(
    Long id,
    String nome,
    String email
) {
    public static PessoaResponse from(Pessoa pessoa) {
        return new PessoaResponse(
            pessoa.getId(),
            pessoa.getNome(),
            pessoa.getEmail()
        );
    }
}
