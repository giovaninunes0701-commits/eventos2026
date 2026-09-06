package com.curso.eventos.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoriaEvento {

    private final String nome;
    private Status status;
    private final List<Evento> eventos = new ArrayList<>();

    public CategoriaEvento(String nome) {
        this.nome = validarTextoObrigatorio(nome, "Nome da categoria é obrigatório");
        this.status = Status.ATIVO;
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
