package com.curso.eventos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evento", uniqueConstraints = @UniqueConstraint(name = "uk_evento_codigo", columnNames = "codigo"))
public class Evento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 40) private String codigo;
    @Column(nullable = false, length = 150) private String titulo;
    @Column(name = "data_evento", nullable = false) private LocalDate dataEvento;
    @Column(nullable = false) private Integer capacidade;
    @Column(name = "capacidade_minima", nullable = false) private Integer capacidadeMinima;
    @Column(name = "vagas_disponiveis", nullable = false) private Integer vagasDisponiveis;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status = Status.ATIVO;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "local_id", nullable = false, foreignKey = @ForeignKey(name = "fk_evento_local")) private LocalEvento local;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "organizador_id", foreignKey = @ForeignKey(name = "fk_evento_organizador")) private Organizador organizador;
    protected Evento() {}
    public Evento(String codigo, String titulo, LocalDate dataEvento, Integer capacidade, Integer capacidadeMinima) {
        this.codigo = LocalEvento.obrigatorio(codigo); this.titulo = LocalEvento.obrigatorio(titulo);
        if (dataEvento == null || capacidade == null || capacidade < 0 || capacidadeMinima == null || capacidadeMinima < 0 || capacidadeMinima > capacidade)
            throw new IllegalArgumentException("Data ou capacidade inválida");
        this.dataEvento = dataEvento; this.capacidade = capacidade; this.capacidadeMinima = capacidadeMinima;
        this.vagasDisponiveis = capacidade;
    }
    public void alterarDados(String titulo, LocalDate dataEvento, Integer capacidade, Integer capacidadeMinima) {
        String tituloValidado = LocalEvento.obrigatorio(titulo);
        if (dataEvento == null || capacidade == null || capacidade < 0 || capacidadeMinima == null || capacidadeMinima < 0 || capacidadeMinima > capacidade)
            throw new IllegalArgumentException("Data ou capacidade inválida");
        int ocupadas = this.capacidade - this.vagasDisponiveis;
        if (capacidade < ocupadas) throw new IllegalArgumentException("Nova capacidade menor que as vagas já ocupadas");
        this.titulo = tituloValidado;
        this.dataEvento = dataEvento;
        this.capacidade = capacidade;
        this.capacidadeMinima = capacidadeMinima;
        this.vagasDisponiveis = capacidade - ocupadas;
    }
    public void alterarStatus(Status novoStatus) {
        if (novoStatus == null) throw new IllegalArgumentException("Status obrigatório");
        this.status = novoStatus;
    }
    public void reservarVagas(int quantidade) {
        if (status != Status.ATIVO) throw new IllegalArgumentException("Evento inativo");
        if (quantidade <= 0 || quantidade > vagasDisponiveis) throw new IllegalArgumentException("Quantidade de vagas inválida");
        vagasDisponiveis -= quantidade;
    }
    public void liberarVagas(int quantidade) {
        if (quantidade <= 0 || quantidade > capacidade - vagasDisponiveis) throw new IllegalArgumentException("Quantidade de vagas inválida");
        vagasDisponiveis += quantidade;
    }
    public void vincular(LocalEvento local, Organizador organizador) { if (local == null) throw new IllegalArgumentException("Local obrigatório"); this.local = local; this.organizador = organizador; }
    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public LocalDate getDataEvento() { return dataEvento; }
    public Integer getCapacidade() { return capacidade; }
    public Integer getCapacidadeMinima() { return capacidadeMinima; }
    public Integer getVagasDisponiveis() { return vagasDisponiveis; }
    public Status getStatus() { return status; }
    public LocalEvento getLocal() { return local; }
    public Organizador getOrganizador() { return organizador; }
}
