package com.curso.eventos.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class Evento {

    private final String codigo;
    private String nome;
    private BigDecimal ingressosDisponiveis;
    private BigDecimal valorIngresso;
    private final LocalDate dataEvento;
    private Status status;
    private CategoriaEvento categoria;

    public Evento(
            String codigo,
            String nome,
            BigDecimal ingressosDisponiveis,
            BigDecimal valorIngresso,
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
        this.dataEvento = Objects.requireNonNull(
                dataEvento,
                "Data do evento é obrigatória");
        this.status = Status.ATIVO;
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

    void associarA(CategoriaEvento categoria) {
        Objects.requireNonNull(categoria, "Categoria é obrigatória");

        if (this.categoria != null && this.categoria != categoria) {
            throw new IllegalStateException("Evento já pertence a outra categoria");
        }

        this.categoria = categoria;
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