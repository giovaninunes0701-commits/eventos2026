package com.curso.eventos.application;

import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.repository.CategoriaEventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaEventoService {

    private final CategoriaEventoRepository repository;

    public CategoriaEventoService(CategoriaEventoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaEvento cadastrar(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RecursoDuplicadoException(
                    "Nome da categoria já cadastrado");
        }
        return repository.save(new CategoriaEvento(nome));
    }

    @Transactional(readOnly = true)
    public CategoriaEvento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de evento não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<CategoriaEvento> listar() {
        return repository.findAll();
    }
}