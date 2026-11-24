package com.curso.eventos.application;

import com.curso.eventos.domain.Pessoa;
import com.curso.eventos.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PessoaService {

    private final PessoaRepository pessoas;

    public PessoaService(PessoaRepository pessoas) {
        this.pessoas = pessoas;
    }

    @Transactional
    public Pessoa cadastrar(String nome, String email, String cpf) {
        Pessoa pessoa = new Pessoa(nome, email, cpf);

        if (pessoas.existsByCpf(pessoa.getCpf())) {
            throw new RecursoDuplicadoException(
                "CPF já cadastrado"
            );
        }

        if (pessoas.existsByEmail(pessoa.getEmail())) {
            throw new RecursoDuplicadoException(
                "E-mail já cadastrado"
            );
        }

        return pessoas.save(pessoa);
    }

    @Transactional(readOnly = true)
    public Pessoa buscarPorId(Long id) {
        return pessoas.findById(id)
            .orElseThrow(() ->
                new RecursoNaoEncontradoException(
                    "Pessoa não encontrada"
                )
            );
    }

    @Transactional(readOnly = true)
    public List<Pessoa> listar() {
        return pessoas.findAll();
    }
}
