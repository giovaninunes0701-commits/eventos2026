package com.curso.eventos.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OpenApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void documentacaoOpenApiDeveEstarDisponivel() throws Exception {
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info.title")
                .value("Eventos 2026 - API REST"))
            .andExpect(jsonPath("$.paths['/api/eventos']").exists())
            .andExpect(jsonPath("$.paths['/api/locais']").exists())
            .andExpect(jsonPath("$.paths['/api/organizadores']").exists());
    }
}
