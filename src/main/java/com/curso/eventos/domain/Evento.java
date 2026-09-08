package com.curso.eventos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "evento",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_evento_codigo",
                columnNames = "codigo"))
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "ingressos_disponiveis", nullable = false, precision = 18, scale = 3)
    private BigDecimal ingressosDisponiveis;

    @Column(name = "valor_ingresso", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorIngresso;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_evento_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_evento_categoria_evento"))
    private CategoriaEvento categoria;

    @Column(name = "ingressos_minimos_alerta", nullable = false, precision = 18, scale = 3)
    private BigDecimal ingressosMinimosAlerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organizador_id",
            foreignKey = @ForeignKey(name = "fk_evento_organizador"))
    private Organizador organizador;

    public Evento(
            String codigo,
            String nome,
            BigDecimal ingressosDisponiveis,
            BigDecimal valorIngresso,
            LocalDate dataEvento) {
        this(codigo, nome, ingressosDisponiveis, valorIngresso, BigDecimal.ZERO, dataEvento);
    }

    public Evento(
            String codigo,
            String nome,
            BigDecimal ingressosDisponiveis,
            BigDecimal valorIngresso,
            BigDecimal ingressosMinimosAlerta,
            LocalDate dataEvento) {
        this.codigo = validarTextoObrigatorio(
                codigo,
                "Código do evento é obrigatório");
        this.nome = validarTextoObrigatorio(
                nome,
                "Nome do evento é obrigatório");
        this.ingressosDisponiveis = validarNaoNegativo(
                ingressosDisponiveis,
                "Ingressos disponíveis não pode ser negativo");
        this.valorIngresso = validarNaoNegativo(
                valorIngresso,
                "Valor do ingresso não pode ser negativo");
        this.ingressosMinimosAlerta = validarNaoNegativo(
                ingressosMinimosAlerta,
                "Ingressos mínimos de alerta não pode ser negativo");
        this.dataEvento = Objects.requireNonNull(
                dataEvento,
                "Data do evento é obrigatória");
        this.status = Status.ATIVO;
    }

    protected Evento() {
    }

    public BigDecimal calcularValorArrecadacaoPotencial() {
        return ingressosDisponiveis
                .multiply(valorIngresso)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void venderIngressos(BigDecimal quantidade) {
        validarPositivo(quantidade, "Quantidade vendida deve ser maior que zero");

        if (ingressosDisponiveis.compareTo(quantidade) < 0) {
            throw new IllegalArgumentException("Ingressos disponíveis insuficientes");
        }

        this.ingressosDisponiveis = ingressosDisponiveis.subtract(quantidade);
    }

    public void reporIngressos(BigDecimal quantidade) {
        validarPositivo(quantidade, "Quantidade reposta deve ser maior que zero");
        this.ingressosDisponiveis = ingressosDisponiveis.add(quantidade);
    }

    public void alterarNome(String novoNome) {
        this.nome = validarTextoObrigatorio(
                novoNome,
                "Nome do evento é obrigatório");
    }

    public void alterarValorIngresso(BigDecimal novoValor) {
        this.valorIngresso = validarNaoNegativo(
                novoValor,
                "Valor do ingresso não pode ser negativo");
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public void associarOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    void associarA(CategoriaEvento categoria) {
        Objects.requireNonNull(categoria, "Categoria é obrigatória");

        if (this.categoria != null && this.categoria != categoria) {
            throw new IllegalStateException("Evento já pertence a outra categoria");
        }

        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getIngressosDisponiveis() {
        return ingressosDisponiveis;
    }

    public BigDecimal getValorIngresso() {
        return valorIngresso;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public Status getStatus() {
        return status;
    }

    public CategoriaEvento getCategoria() {
        return categoria;
    }

    public BigDecimal getIngressosMinimosAlerta() {
        return ingressosMinimosAlerta;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }

    private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static void validarPositivo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}