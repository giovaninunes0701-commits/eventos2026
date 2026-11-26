package com.curso.eventos.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PessoaApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void deveCadastrarPessoaPelaApi() throws Exception {
        mvc.perform(post("/api/pessoas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "nome": "Pessoa Aula 10",
                  "email": "pessoa.api10@exemplo.com",
                  "cpf": "52998224725"
                }
                """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.nome")
                .value("Pessoa Aula 10"))
            .andExpect(jsonPath("$.email")
                .value("pessoa.api10@exemplo.com"));
    }
}
