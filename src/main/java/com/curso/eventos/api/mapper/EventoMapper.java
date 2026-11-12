package com.curso.eventos.api.mapper;
import com.curso.eventos.domain.*;
import com.curso.eventos.api.dto.*;
import org.springframework.stereotype.Component;
@Component public class EventoMapper {
    public Evento toEntity(EventoRequest r) { return new Evento(r.codigo(), r.titulo(), r.dataEvento(), r.capacidade(), r.capacidadeMinima()); }
    public EventoResponse toResponse(Evento e) {
        Organizador o = e.getOrganizador();
        return new EventoResponse(e.getId(), e.getCodigo(), e.getTitulo(), e.getDataEvento(), e.getCapacidade(),
            e.getCapacidadeMinima(), e.getVagasDisponiveis(), e.getStatus(), e.getLocal().getId(), e.getLocal().getNome(),
            o == null ? null : o.getId(), o == null ? null : o.getNome());
    }
}
