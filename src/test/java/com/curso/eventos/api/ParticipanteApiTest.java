package com.curso.eventos.api;

import com.curso.eventos.application.PessoaService;
import com.curso.eventos.domain.Pessoa;

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
class ParticipanteApiTest {

    @Autowired MockMvc mvc;
    @Autowired PessoaService pessoas;

    @Test
    void deveCadastrarParticipantePelaApi() throws Exception {

        Pessoa pessoa = pessoas.cadastrar(
            "Participante API",
            "participante.api@exemplo.com",
            "52998224725"
        );

        String json = """
            {
              "pessoaId": %d,
              "telefone": "17999999999"
            }
            """.formatted(pessoa.getId());

        mvc.perform(post("/api/participantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.pessoaId")
                .value(pessoa.getId()))
            .andExpect(jsonPath("$.nome")
                .value("Participante API"));
    }
}
