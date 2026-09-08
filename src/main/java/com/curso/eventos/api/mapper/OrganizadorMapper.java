package com.curso.eventos.api.mapper;

import com.curso.eventos.api.dto.OrganizadorRequest;
import com.curso.eventos.api.dto.OrganizadorResponse;
import com.curso.eventos.domain.Organizador;
import org.springframework.stereotype.Component;

@Component
public class OrganizadorMapper {

    public Organizador toEntity(OrganizadorRequest request) {
        return new Organizador(request.razaoSocial(), request.cnpj());
    }

    public OrganizadorResponse toResponse(Organizador organizador) {
        return new OrganizadorResponse(
                organizador.getId(),
                organizador.getRazaoSocial(),
                organizador.getCnpj(),
                organizador.getStatus());
    }
}
