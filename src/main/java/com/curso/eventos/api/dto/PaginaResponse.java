package com.curso.eventos.api.dto;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(List<T> conteudo, int pagina, int tamanho,
        long totalElementos, int totalPaginas, boolean primeira, boolean ultima) {
    public static <E,T> PaginaResponse<T> de(Page<E> pagina, Function<E,T> mapper) {
        return new PaginaResponse<>(pagina.getContent().stream().map(mapper).toList(),
                pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(),
                pagina.getTotalPages(), pagina.isFirst(), pagina.isLast());
    }
}
