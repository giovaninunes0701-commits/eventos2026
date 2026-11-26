package com.curso.eventos.api.dto;

import com.curso.eventos.domain.PerfilAcesso;
import com.curso.eventos.domain.StatusContaAcesso;
import java.util.Set;

public record ContaAcessoResponse(
    Long id,
    Long pessoaId,
    String nomePessoa,
    String login,
    StatusContaAcesso status,
    Set<PerfilAcesso> perfis
) {
    public ContaAcessoResponse {
        perfis = Set.copyOf(perfis);
    }
}
