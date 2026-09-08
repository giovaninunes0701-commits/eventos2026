package com.curso.eventos.api.mapper;

import com.curso.eventos.api.dto.CategoriaEventoRequest;
import com.curso.eventos.api.dto.CategoriaEventoResponse;
import com.curso.eventos.domain.CategoriaEvento;
import org.springframework.stereotype.Component;

@Component
public class CategoriaEventoMapper {

    public CategoriaEvento toEntity(CategoriaEventoRequest request) {
        return new CategoriaEvento(request.nome());
    }

    public CategoriaEventoResponse toResponse(CategoriaEvento categoria) {
        return new CategoriaEventoResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getStatus());
    }
}
