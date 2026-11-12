package com.curso.eventos.api.mapper;
import com.curso.eventos.domain.LocalEvento;
import com.curso.eventos.api.dto.*;
import org.springframework.stereotype.Component;
@Component public class LocalEventoMapper {
    public LocalEvento toEntity(LocalEventoRequest r) { return new LocalEvento(r.nome(), r.endereco()); }
    public LocalEventoResponse toResponse(LocalEvento e) { return new LocalEventoResponse(e.getId(), e.getNome(), e.getEndereco()); }
}
