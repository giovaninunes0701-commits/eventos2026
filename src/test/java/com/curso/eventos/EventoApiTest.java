package com.curso.eventos;

import com.curso.eventos.domain.CategoriaEvento;
import com.curso.eventos.repository.CategoriaEventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaEventoRepository categoriaRepository;

    @Test
    void deveCadastrarEventoERetornar201() throws Exception {
        CategoriaEvento categoria = categoriaRepository.save(
                new CategoriaEvento("Categoria API Test"));

        String json = """
                {
                  "codigo": "API-TEST-001",
                  "nome": "Evento criado pela API",
                  "ingressosDisponiveis": 10.000,
                  "valorIngresso": 49.90,
                  "ingressosMinimosAlerta": 2.000,
                  "dataEvento": "2026-12-01",
                  "categoriaId": %d
                }
                """.formatted(categoria.getId());

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigo").value("API-TEST-001"));
    }

    @Test
    void deveRetornar400QuandoEntradaInvalida() throws Exception {
        String json = """
                {
                  "codigo": "",
                  "nome": "",
                  "ingressosDisponiveis": -1,
                  "valorIngresso": -1,
                  "ingressosMinimosAlerta": -1,
                  "dataEvento": "2026-12-01",
                  "categoriaId": 0
                }
                """;

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.nome").exists());
    }

    @Test
    void deveRetornar404QuandoEventoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/eventos/999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
        String json = """
                {
                  "codigo": "API-TEST-002",
                  "nome": "Evento sem categoria",
                  "ingressosDisponiveis": 10.000,
                  "valorIngresso": 49.90,
                  "ingressosMinimosAlerta": 2.000,
                  "dataEvento": "2026-12-01",
                  "categoriaId": 999999999
                }
                """;

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409QuandoCodigoDuplicado() throws Exception {
        CategoriaEvento categoria = categoriaRepository.save(
                new CategoriaEvento("Categoria Duplicado Test"));

        String json = """
                {
                  "codigo": "API-DUP-001",
                  "nome": "Evento duplicado",
                  "ingressosDisponiveis": 10.000,
                  "valorIngresso": 49.90,
                  "ingressosMinimosAlerta": 2.000,
                  "dataEvento": "2026-12-01",
                  "categoriaId": %d
                }
                """.formatted(categoria.getId());

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
