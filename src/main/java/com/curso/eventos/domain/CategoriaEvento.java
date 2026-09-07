package com.curso.eventos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categoria_evento")
public class CategoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Evento> eventos = new ArrayList<>();

    public CategoriaEvento(String nome) {
        this.nome = validarTextoObrigatorio(nome, "Nome da categoria é obrigatório");
        this.status = Status.ATIVO;
    }

    protected CategoriaEvento() {
    }

    public void adicionarEvento(Evento evento) {
        Objects.requireNonNull(evento, "Evento é obrigatório");

        boolean codigoJaUtilizado = eventos.stream()
                .anyMatch(item -> item != evento
                        && item.getCodigo().equals(evento.getCodigo()));

        if (codigoJaUtilizado) {
            throw new IllegalArgumentException("Código de evento já utilizado na categoria");
        }

        evento.associarA(this);

        if (!eventos.contains(evento)) {
            eventos.add(evento);
        }
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

    public List<Evento> getEventos() {
        return List.copyOf(eventos);
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }
}