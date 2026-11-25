package com.curso.eventos.application;
import com.curso.eventos.domain.*;
import com.curso.eventos.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Service
public class EventoService {
    private final EventoRepository eventos;
    private final LocalEventoRepository locais;
    private final OrganizadorRepository organizadores;
    private final EntityManager entityManager;
    public EventoService(EventoRepository eventos, LocalEventoRepository locais, OrganizadorRepository organizadores, EntityManager entityManager) {
        this.eventos = eventos; this.locais = locais; this.organizadores = organizadores; this.entityManager = entityManager;
    }
    @Transactional public Evento cadastrar(Evento evento, Long localId, Long organizadorId) {
        if (eventos.existsByCodigo(evento.getCodigo())) throw new RecursoDuplicadoException("Código de evento já cadastrado");
        LocalEvento local = locais.findById(localId).orElseThrow(() -> new RecursoNaoEncontradoException("Local não encontrado"));
        Organizador organizador = organizadorId == null ? null : organizadores.findById(organizadorId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Organizador não encontrado"));
        evento.vincular(local, organizador);
        return eventos.save(evento);
    }
    @Transactional(readOnly = true) public Evento buscarPorId(Long id) { return eventos.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado")); }
    @Transactional(readOnly = true) public List<Evento> listar() { return eventos.findAll(); }
    private Evento buscarParaAtualizacao(Long id) {
        Evento evento = buscarPorId(id);
        entityManager.flush();
        entityManager.refresh(evento, LockModeType.PESSIMISTIC_WRITE);
        return evento;
    }

    @Transactional public Evento alterar(Long id, String titulo, LocalDate dataEvento,
            Integer capacidade, Integer capacidadeMinima, Long localId, Long organizadorId) {
        Evento evento = buscarParaAtualizacao(id);
        LocalEvento local = locais.findById(localId).orElseThrow(() -> new RecursoNaoEncontradoException("Local não encontrado"));
        Organizador organizador = organizadorId == null ? null : organizadores.findById(organizadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Organizador não encontrado"));
        evento.alterarDados(titulo, dataEvento, capacidade, capacidadeMinima);
        evento.vincular(local, organizador);
        return evento;
    }
    @Transactional public Evento alterarStatus(Long id, Status status) {
        Evento evento = buscarParaAtualizacao(id);
        evento.alterarStatus(status);
        return evento;
    }
    @Transactional public Evento reservarVagas(Long id, int quantidade) {
        Evento evento = buscarParaAtualizacao(id);
        evento.reservarVagas(quantidade);
        return evento;
    }
    @Transactional public Evento liberarVagas(Long id, int quantidade) {
        Evento evento = buscarParaAtualizacao(id);
        evento.liberarVagas(quantidade);
        return evento;
    }
    @Transactional public void excluir(Long id) {
        Evento evento = buscarParaAtualizacao(id);
        eventos.delete(evento);
        eventos.flush();
    }
    @Transactional(readOnly = true) public Page<Evento> pesquisar(String titulo, Status status,
            Long localId, Long organizadorId, LocalDate dataInicial, LocalDate dataFinal,
            Boolean vagasDisponiveis, Pageable pageable) {
        Set<String> campos = Set.of("id", "codigo", "titulo", "dataEvento", "capacidade",
                "capacidadeMinima", "vagasDisponiveis", "status");
        pageable.getSort().forEach(ordem -> {
            if (!campos.contains(ordem.getProperty()))
                throw new IllegalArgumentException("Campo de ordenação inválido: " + ordem.getProperty());
        });
        Specification<Evento> filtro = (root, query, cb) -> cb.conjunction();
        if (titulo != null && !titulo.isBlank()) {
            String trecho = "%" + titulo.trim().toLowerCase(Locale.ROOT) + "%";
            filtro = filtro.and((root, query, cb) -> cb.like(cb.lower(root.get("titulo")), trecho));
        }
        if (status != null) filtro = filtro.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if (localId != null) filtro = filtro.and((root, query, cb) -> cb.equal(root.get("local").get("id"), localId));
        if (organizadorId != null) filtro = filtro.and((root, query, cb) -> cb.equal(root.get("organizador").get("id"), organizadorId));
        if (dataInicial != null) filtro = filtro.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataEvento"), dataInicial));
        if (dataFinal != null) filtro = filtro.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataEvento"), dataFinal));
        if (Boolean.TRUE.equals(vagasDisponiveis)) filtro = filtro.and((root, query, cb) -> cb.greaterThan(root.get("vagasDisponiveis"), 0));
        return eventos.findAll(filtro, pageable);
    }
}
