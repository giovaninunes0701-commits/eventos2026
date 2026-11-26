package com.curso.eventos.domain;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "conta_acesso")
public class ContaAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @Column(nullable = false, length = 80)
    private String login;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusContaAcesso status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "conta_acesso_perfil",
        joinColumns = @JoinColumn(name = "conta_acesso_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 30)
    private Set<PerfilAcesso> perfis = new LinkedHashSet<>();

    protected ContaAcesso() {
    }

    public ContaAcesso(Pessoa pessoa, String login, String senhaHash) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa obrigatoria");
        }
        if (login == null || !login.trim().matches("[A-Za-z0-9._-]{3,80}")) {
            throw new IllegalArgumentException("Login invalido");
        }
        if (senhaHash == null || !senhaHash.startsWith("{bcrypt}")) {
            throw new IllegalArgumentException("Hash de senha invalido");
        }

        this.pessoa = pessoa;
        this.login = login.trim().toLowerCase(java.util.Locale.ROOT);
        this.senhaHash = senhaHash;
        this.status = StatusContaAcesso.ATIVA;
        this.perfis.add(PerfilAcesso.OPERADOR);
    }

    public boolean podeAutenticar() {
        return status == StatusContaAcesso.ATIVA;
    }

    public void bloquear() {
        this.status = StatusContaAcesso.BLOQUEADA;
    }

    public void ativar() {
        this.status = StatusContaAcesso.ATIVA;
    }

    public void alterarSenha(String novoHash) {
        if (novoHash == null || !novoHash.startsWith("{bcrypt}")) {
            throw new IllegalArgumentException("Hash de senha invalido");
        }
        this.senhaHash = novoHash;
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getLogin() {
        return login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public StatusContaAcesso getStatus() {
        return status;
    }

    public Set<PerfilAcesso> getPerfis() {
        return java.util.Collections.unmodifiableSet(perfis);
    }

}
