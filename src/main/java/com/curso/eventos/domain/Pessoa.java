package com.curso.eventos.domain;

import jakarta.persistence.*;
import java.util.Locale;

@Entity
@Table(name = "pessoa", uniqueConstraints = {
    @UniqueConstraint(name = "uk_pessoa_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "cpf")
})
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    protected Pessoa() {
    }

    public Pessoa(String nome, String email, String cpf) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório");
        }

        if (email == null ||
            !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido");
        }

        if (!DocumentoFiscal.cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        this.nome = nome.trim();
        this.email = email.trim().toLowerCase(Locale.ROOT);
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }
}
