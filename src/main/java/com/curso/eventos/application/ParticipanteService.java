package com.curso.eventos.application;

import com.curso.eventos.domain.Participante;
import com.curso.eventos.domain.Pessoa;
import com.curso.eventos.repository.ParticipanteRepository;
import com.curso.eventos.repository.PessoaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participantes;
    private final PessoaRepository pessoas;

    public ParticipanteService(
            ParticipanteRepository participantes,
            PessoaRepository pessoas) {
        this.participantes = participantes;
        this.pessoas = pessoas;
    }

    @Transactional
    public Participante cadastrar(Long pessoaId, String telefone) {
        Pessoa pessoa = pessoas.findById(pessoaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Pessoa não encontrada"
            ));

        if (participantes.findByPessoaId(pessoaId).isPresent()) {
            throw new RecursoDuplicadoException(
                "Pessoa já cadastrada como participante"
            );
        }

        Participante participante = new Participante(pessoa, telefone);

        return participantes.save(participante);
    }

    @Transactional(readOnly = true)
    public Participante buscarPorId(Long id) {
        return participantes.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Participante não encontrado"
            ));
    }

    @Transactional(readOnly = true)
    public List<Participante> listar() {
        return participantes.findAll();
    }
}
