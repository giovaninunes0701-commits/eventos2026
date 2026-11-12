package com.curso.eventos.application;
import com.curso.eventos.domain.LocalEvento;
import com.curso.eventos.repository.LocalEventoRepository;
import com.curso.eventos.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LocalEventoService {
    private final LocalEventoRepository repository;
    private final EventoRepository eventos;
    public LocalEventoService(LocalEventoRepository repository, EventoRepository eventos) { this.repository = repository; this.eventos = eventos; }
    @Transactional public LocalEvento cadastrar(LocalEvento local) {
        if (repository.existsByNome(local.getNome())) throw new RecursoDuplicadoException("Local já cadastrado");
        return repository.save(local);
    }
    @Transactional(readOnly = true) public LocalEvento buscarPorId(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Local não encontrado")); }
    @Transactional(readOnly = true) public List<LocalEvento> listar() { return repository.findAll(); }
    @Transactional public LocalEvento alterar(Long id, String nome, String endereco) {
        LocalEvento local = buscarPorId(id);
        if (!local.getNome().equalsIgnoreCase(nome.trim()) && repository.existsByNome(nome.trim()))
            throw new RecursoDuplicadoException("Local já cadastrado");
        local.alterar(nome, endereco);
        return local;
    }
    @Transactional public void excluir(Long id) {
        LocalEvento local = buscarPorId(id);
        if (eventos.existsByLocalId(id)) throw new RecursoEmUsoException("Local possui eventos vinculados");
        repository.delete(local);
        repository.flush();
    }
}
