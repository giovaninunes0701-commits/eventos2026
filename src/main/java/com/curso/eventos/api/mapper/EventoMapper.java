package com.curso.eventos.api.mapper;

import com.curso.eventos.api.dto.EventoRequest;
import com.curso.eventos.api.dto.EventoResponse;
import com.curso.eventos.domain.Evento;
import com.curso.eventos.domain.Organizador;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public Evento toEntity(EventoRequest request) {
        return new Evento(
                request.codigo(),
                request.nome(),
                request.ingressosDisponiveis(),
                request.valorIngresso(),
                request.ingressosMinimosAlerta(),
                request.dataEvento());
    }

    public EventoResponse toResponse(Evento evento) {
        Organizador organizador = evento.getOrganizador();

        return new EventoResponse(
                evento.getId(),
                evento.getCodigo(),
                evento.getNome(),
                evento.getIngressosDisponiveis(),
                evento.getValorIngresso(),
                evento.getIngressosMinimosAlerta(),
                evento.calcularValorArrecadacaoPotencial(),
                evento.getDataEvento(),
                evento.getStatus(),
                evento.getCategoria().getId(),
                evento.getCategoria().getNome(),
                organizador == null ? null : organizador.getId(),
                organizador == null ? null : organizador.getRazaoSocial());
    }
}
