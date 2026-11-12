package com.curso.eventos.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "organizador", uniqueConstraints = @UniqueConstraint(name = "uk_organizador_email", columnNames = "email"))
public class Organizador {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String nome;
    @Column(nullable = false, length = 150) private String email;
    protected Organizador() {}
    public Organizador(String nome, String email) {
        this.nome = LocalEvento.obrigatorio(nome);
        this.email = LocalEvento.obrigatorio(email);
        if (!this.email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido");
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
