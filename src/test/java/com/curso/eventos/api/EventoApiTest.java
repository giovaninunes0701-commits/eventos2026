package com.curso.eventos.api;

import com.curso.eventos.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class EventoApiTest {
    @Autowired MockMvc mvc;
    @Autowired EventoRepository eventos;
    @Autowired JdbcTemplate jdbc;

    @Test void migracoesRegistradasEBancoCoerente() {
        Integer qtd=jdbc.queryForObject("select count(*) from databasechangelog",Integer.class);
        org.junit.jupiter.api.Assertions.assertEquals(13,qtd);
        Integer minimo=jdbc.queryForObject("select capacidade_minima from evento where codigo='LEGADO-001'",Integer.class);
        org.junit.jupiter.api.Assertions.assertEquals(0,minimo);
    }

    @Test void healthRetorna200() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
    @Test void listaEventoLegadoPreservado() throws Exception {
        mvc.perform(get("/api/eventos")).andExpect(status().isOk())
            .andExpect(jsonPath("$.conteudo[?(@.codigo == 'LEGADO-001')].capacidadeMinima",hasItem(0)));
    }
    @Test void cadastrarLocalRetorna201Location() throws Exception {
        mvc.perform(post("/api/locais").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Ginásio Central\",\"endereco\":\"Rua A, 123\"}"))
            .andExpect(status().isCreated()).andExpect(header().string("Location",containsString("/api/locais/")))
            .andExpect(jsonPath("$.nome").value("Ginásio Central"));
    }
    @Test void validarCamposRetorna400() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON)
            .content("{\"codigo\":\"\",\"titulo\":\"\",\"capacidade\":-1,\"capacidadeMinima\":-3,\"localId\":0}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.codigo").exists());
    }
    @Test void eventoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/eventos/9999999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }
    @Test void codigoDuplicadoRetorna409() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON)
            .content("{\"codigo\":\"LEGADO-001\",\"titulo\":\"Repetido\",\"dataEvento\":\"2026-11-20\",\"capacidade\":100,\"capacidadeMinima\":0,\"localId\":1}"))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }
    @Test void jsonMalformadoRetorna400() throws Exception {
        mvc.perform(post("/api/locais").contentType(MediaType.APPLICATION_JSON).content("{inválido"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void cadastrarEventoSemOrganizador() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON)
            .content("{\"codigo\":\"API-001\",\"titulo\":\"Feira\",\"dataEvento\":\"2026-11-20\",\"capacidade\":100,\"capacidadeMinima\":10,\"localId\":1}"))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.capacidadeMinima").value(10)).andExpect(jsonPath("$.organizadorId").value(nullValue()));
    }
    @Test void pesquisarEventosComPaginacao() throws Exception {
        mvc.perform(get("/api/eventos?size=2&sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conteudo").isArray())
            .andExpect(jsonPath("$.tamanho").value(2));
    }
    @Test void rejeitarCampoInvalidoDeOrdenacao() throws Exception {
        mvc.perform(get("/api/eventos?sort=campoInexistente,asc"))
            .andExpect(status().isBadRequest());
    }
    @Test void atualizarEControlarVagasDeEvento() throws Exception {
        String body = "{\"codigo\":\"A08-01\",\"titulo\":\"Feira\",\"dataEvento\":\"2026-11-20\",\"capacidade\":10,\"capacidadeMinima\":2,\"localId\":1}";
        String resposta = mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Long id = ((Number) com.jayway.jsonpath.JsonPath.read(resposta, "$.id")).longValue();
        mvc.perform(post("/api/eventos/" + id + "/vagas/reservas")
                .contentType(MediaType.APPLICATION_JSON).content("{\"quantidade\":3}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.vagasDisponiveis").value(7));
        mvc.perform(put("/api/eventos/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"INATIVO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("INATIVO"));
        mvc.perform(post("/api/eventos/" + id + "/vagas/reservas")
                .contentType(MediaType.APPLICATION_JSON).content("{\"quantidade\":1}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/eventos/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Feira Nova\",\"dataEvento\":\"2026-11-21\",\"capacidade\":12,\"capacidadeMinima\":2,\"localId\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.vagasDisponiveis").value(9));
        mvc.perform(delete("/api/eventos/" + id)).andExpect(status().isNoContent());
    }
}
