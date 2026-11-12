package com.curso.eventos.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "local_evento", uniqueConstraints = @UniqueConstraint(name = "uk_local_nome", columnNames = "nome"))
public class LocalEvento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String nome;
    @Column(nullable = false, length = 200) private String endereco;
    protected LocalEvento() {}
    public LocalEvento(String nome, String endereco) { this.nome = obrigatorio(nome); this.endereco = obrigatorio(endereco); }
    public void alterar(String novoNome, String novoEndereco) {
        this.nome = obrigatorio(novoNome);
        this.endereco = obrigatorio(novoEndereco);
    }
    static String obrigatorio(String s) { if (s == null || s.isBlank()) throw new IllegalArgumentException("Texto obrigatório"); return s.trim(); }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
}
