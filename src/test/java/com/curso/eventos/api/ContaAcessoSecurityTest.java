package com.curso.eventos.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import com.curso.eventos.application.ContaAcessoService;
import com.curso.eventos.domain.ContaAcesso;
import com.curso.eventos.domain.Pessoa;
import com.curso.eventos.repository.PessoaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContaAcessoSecurityTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PessoaRepository pessoas;

    @Autowired
    ContaAcessoService contas;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void healthPublicoRetorna200() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void eventosSemAutenticacaoRetorna401() throws Exception {
        mvc.perform(get("/api/eventos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void cadastroArmazenaSenhaComBCrypt() {
        Pessoa pessoa = pessoas.save(
                new Pessoa("Teste Seguranca", "seguranca.aula12@example.com", "52998224725")
        );

        String senha = "SenhaSegura2026!";
        ContaAcesso conta = contas.cadastrar(
                pessoa.getId(), "teste.seg.aula12", senha
        );

        assertNotNull(conta.getId());
        assertNotEquals(senha, conta.getSenhaHash());
        assertTrue(conta.getSenhaHash().startsWith("{bcrypt}"));
        assertTrue(passwordEncoder.matches(senha, conta.getSenhaHash()));
    }


    @Test
    @Transactional
    void loginCorretoPermiteConsultarMinhaConta() throws Exception {
        Pessoa pessoa = pessoas.save(
                new Pessoa("Usuario Login", "login.aula12@example.com", "52998224725")
        );
        contas.cadastrar(pessoa.getId(), "login.aula12", "SenhaSegura2026!");

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("login.aula12", "SenhaSegura2026!")))
                .andExpect(status().isOk())
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .jsonPath("$.login").value("login.aula12")
                )
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .jsonPath("$.senha").doesNotExist()
                )
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .jsonPath("$.senhaHash").doesNotExist()
                );
    }

    @Test
    @Transactional
    void senhaIncorretaRetorna401() throws Exception {
        Pessoa pessoa = pessoas.save(
                new Pessoa("Usuario Senha", "senha.aula12@example.com", "52998224725")
        );
        contas.cadastrar(pessoa.getId(), "senha.aula12", "SenhaSegura2026!");

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("senha.aula12", "SenhaIncorreta2026!")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void contaBloqueadaRetorna401() throws Exception {
        Pessoa pessoa = pessoas.save(
                new Pessoa("Usuario Bloqueado", "bloqueado.aula12@example.com", "52998224725")
        );
        ContaAcesso conta = contas.cadastrar(
                pessoa.getId(), "bloqueado.aula12", "SenhaSegura2026!"
        );
        contas.bloquear(conta.getId());

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("bloqueado.aula12", "SenhaSegura2026!")))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @Transactional
    void alterarSenhaInvalidaCredencialAnterior() throws Exception {
        Pessoa pessoa = pessoas.save(
                new Pessoa("Usuario Troca", "troca.aula12@example.com", "52998224725")
        );

        contas.cadastrar(pessoa.getId(), "troca.aula12", "SenhaAntiga2026!");

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("troca.aula12", "SenhaAntiga2026!")))
                .andExpect(status().isOk());

        mvc.perform(put("/api/contas-acesso/me/senha")
                        .with(httpBasic("troca.aula12", "SenhaAntiga2026!"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senhaAtual": "SenhaAntiga2026!",
                                  "novaSenha": "SenhaNova2026!"
                                }
                                """))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("troca.aula12", "SenhaAntiga2026!")))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/contas-acesso/me")
                        .with(httpBasic("troca.aula12", "SenhaNova2026!")))
                .andExpect(status().isOk());
    }


    @Test
    void contratoOpenApiDocumentaAutenticacao() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.components.securitySchemes.basicAuth.scheme")
                        .value("basic"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.paths['/api/contas-acesso'].post.security")
                        .isEmpty())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.paths['/api/pessoas'].post.security")
                        .isEmpty())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.security[0].basicAuth").exists());
    }

}
