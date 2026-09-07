package com.curso.eventos.application;

import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.domain.Evento;
import com.curso.eventos.repository.CategoriaEventoRepository;
import com.curso.eventos.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final CategoriaEventoRepository categoriaRepository;

    public EventoService(
            EventoRepository eventoRepository,
            CategoriaEventoRepository categoriaRepository) {
        this.eventoRepository = eventoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Evento cadastrar(Evento evento, Long categoriaId) {
        if (eventoRepository.existsByCodigo(evento.getCodigo())) {
            throw new RecursoDuplicadoException(
                    "Código de evento já cadastrado");
        }

        CategoriaEvento categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de evento não encontrada"));

        categoria.adicionarEvento(evento);
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Evento não encontrado"));
    }
}