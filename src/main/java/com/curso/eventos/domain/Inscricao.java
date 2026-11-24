package com.curso.eventos.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "inscricao", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_inscricao_participante_evento",
        columnNames = {"participante_id", "evento_id"}
    )
})
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "participante_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_inscricao_participante")
    )
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "evento_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_inscricao_evento")
    )
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusInscricao status = StatusInscricao.SOLICITADA;

    protected Inscricao() {
    }

    public Inscricao(Participante participante, Evento evento) {
        if (participante == null) {
            throw new IllegalArgumentException("Participante obrigatório");
        }

        if (evento == null) {
            throw new IllegalArgumentException("Evento obrigatório");
        }

        this.participante = participante;
        this.evento = evento;
        this.status = StatusInscricao.SOLICITADA;
    }

    public void confirmar() {
        if (status != StatusInscricao.SOLICITADA) {
            throw new IllegalStateException(
                "Somente inscrições solicitadas podem ser confirmadas"
            );
        }

        evento.reservarVagas(1);
        status = StatusInscricao.CONFIRMADA;
    }

    public void cancelar() {
        if (status == StatusInscricao.CANCELADA) {
            throw new IllegalStateException("Inscrição já cancelada");
        }

        if (status == StatusInscricao.CONFIRMADA) {
            evento.liberarVagas(1);
        }

        status = StatusInscricao.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public Participante getParticipante() {
        return participante;
    }

    public Evento getEvento() {
        return evento;
    }

    public StatusInscricao getStatus() {
        return status;
    }
}
