package com.curso.eventos.api.mapper;
import com.curso.eventos.domain.Organizador;
import com.curso.eventos.api.dto.*;
import org.springframework.stereotype.Component;
@Component public class OrganizadorMapper {
    public Organizador toEntity(OrganizadorRequest r) { return new Organizador(r.nome(), r.email()); }
    public OrganizadorResponse toResponse(Organizador e) { return new OrganizadorResponse(e.getId(), e.getNome(), e.getEmail()); }
}
