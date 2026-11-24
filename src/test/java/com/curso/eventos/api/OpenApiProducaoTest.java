package com.curso.eventos.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "prod"})
class OpenApiProducaoTest {

    @Autowired
    MockMvc mvc;

    @Test
    void documentacaoDeveEstarDesabilitadaEmProducao() throws Exception {
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isNotFound());

        mvc.perform(get("/swagger-ui.html"))
            .andExpect(status().isNotFound());
    }
}
