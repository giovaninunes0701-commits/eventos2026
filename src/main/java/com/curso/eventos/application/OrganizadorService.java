package com.curso.eventos.application;

import com.curso.eventos.domain.Organizador;
import com.curso.eventos.repository.OrganizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizadorService {

    private final OrganizadorRepository repository;

    public OrganizadorService(OrganizadorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Organizador cadastrar(Organizador organizador) {
        if (repository.existsByCnpj(organizador.getCnpj())) {
            throw new RecursoDuplicadoException("CNPJ já cadastrado");
        }
        return repository.save(organizador);
    }

    @Transactional(readOnly = true)
    public Organizador buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Organizador não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Organizador> listar() {
        return repository.findAll();
    }
}
