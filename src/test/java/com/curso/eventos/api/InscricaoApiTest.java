package com.curso.eventos.api;

import com.curso.eventos.application.PessoaService;
import com.curso.eventos.application.ParticipanteService;
import com.curso.eventos.domain.*;
import com.curso.eventos.repository.EventoRepository;

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
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InscricaoApiTest {

    @Autowired MockMvc mvc;
    @Autowired PessoaService pessoas;
    @Autowired ParticipanteService participantes;
    @Autowired EventoRepository eventos;

    @Test
    void deveCadastrarConfirmarECancelarInscricao() throws Exception {

        Pessoa pessoa = pessoas.cadastrar(
            "Teste Inscricao API",
            "inscricao.api10@exemplo.com",
            "52998224725"
        );

        Participante participante = participantes.cadastrar(
            pessoa.getId(),
            "17999999999"
        );

        Evento evento = eventos.findAll()
            .stream()
            .filter(e -> e.getStatus() == Status.ATIVO)
            .findFirst()
            .orElseThrow();

        String json = """
            {
                "participanteId": %d,
                "eventoId": %d
            }
            """.formatted(participante.getId(), evento.getId());

        String resposta = mvc.perform(post("/api/inscricoes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("SOLICITADA"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number id = com.jayway.jsonpath.JsonPath.read(
            resposta, "$.id"
        );

        mvc.perform(put("/api/inscricoes/" +
            id.longValue() + "/confirmacao"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMADA"));

        mvc.perform(put("/api/inscricoes/" +
            id.longValue() + "/cancelamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADA"));
    }
}
