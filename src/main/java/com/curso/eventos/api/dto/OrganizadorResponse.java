package com.curso.eventos.api.dto;

import com.curso.eventos.domain.Status;

public record OrganizadorResponse(
        Long id,
        String razaoSocial,
        String cnpj,
        Status status
) {
}
