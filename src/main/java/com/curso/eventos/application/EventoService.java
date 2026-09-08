package com.curso.eventos.application;

import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.domain.Evento;
import com.curso.eventos.domain.Organizador;
import com.curso.eventos.repository.CategoriaEventoRepository;
import com.curso.eventos.repository.EventoRepository;
import com.curso.eventos.repository.OrganizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CategoriaEventoRepository categoriaRepository;
    private final OrganizadorRepository organizadorRepository;

    public EventoService(
            EventoRepository eventoRepository,
            CategoriaEventoRepository categoriaRepository,
            OrganizadorRepository organizadorRepository) {
        this.eventoRepository = eventoRepository;
        this.categoriaRepository = categoriaRepository;
        this.organizadorRepository = organizadorRepository;
    }

    @Transactional
    public Evento cadastrar(Evento evento, Long categoriaId, Long organizadorId) {
        if (eventoRepository.existsByCodigo(evento.getCodigo())) {
            throw new RecursoDuplicadoException(
                    "Código de evento já cadastrado");
        }

        CategoriaEvento categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de evento não encontrada"));

        categoria.adicionarEvento(evento);

        if (organizadorId != null) {
            Organizador organizador = organizadorRepository.findById(organizadorId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Organizador não encontrado"));
            evento.associarOrganizador(organizador);
        }

        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Evento não encontrado"));
    }

    @Transactional(readOnly = true)
    public java.util.List<Evento> listar() {
        return eventoRepository.findAll();
    }
}
