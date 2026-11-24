package com.curso.eventos.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoFiscalTest {

    @Test
    void deveValidarCpf() {
        assertTrue(DocumentoFiscal.cpfValido("52998224725"));
        assertFalse(DocumentoFiscal.cpfValido("52998224724"));
        assertFalse(DocumentoFiscal.cpfValido("11111111111"));
        assertFalse(DocumentoFiscal.cpfValido(null));
    }

    @Test
    void deveValidarCnpj() {
        assertTrue(DocumentoFiscal.cnpjValido("11222333000181"));
        assertFalse(DocumentoFiscal.cnpjValido("11222333000182"));
        assertFalse(DocumentoFiscal.cnpjValido("00000000000000"));
        assertFalse(DocumentoFiscal.cnpjValido(null));
    }
}
