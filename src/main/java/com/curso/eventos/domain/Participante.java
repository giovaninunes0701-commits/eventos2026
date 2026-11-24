package com.curso.eventos.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "participante", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_participante_pessoa",
        columnNames = "pessoa_id"
    )
})
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "pessoa_id",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_participante_pessoa")
    )
    private Pessoa pessoa;

    @Column(length = 30)
    private String telefone;

    protected Participante() {
    }

    public Participante(Pessoa pessoa, String telefone) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa obrigatória");
        }

        if (telefone != null && telefone.length() > 30) {
            throw new IllegalArgumentException("Telefone muito longo");
        }

        this.pessoa = pessoa;
        this.telefone = telefone == null ? null : telefone.trim();
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getTelefone() {
        return telefone;
    }
}
