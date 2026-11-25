package com.curso.eventos.integration;

import com.curso.eventos.application.InscricaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("testpg")
class InscricaoConcorrenciaTest {

    @Autowired JdbcTemplate jdbc;
    @Autowired InscricaoService inscricoes;
    @Autowired com.curso.eventos.application.EventoService eventoService;
    @Autowired PlatformTransactionManager transacoes;

    record Dados(Long local, Long evento, Long pessoaA,
                 Long pessoaB, Long participanteA,
                 Long participanteB, Long inscricaoA,
                 Long inscricaoB) {}

    private Dados preparar() {
        String codigo = UUID.randomUUID().toString();

        return new TransactionTemplate(transacoes).execute(status -> {
            Long local = jdbc.queryForObject("""
                INSERT INTO local_evento (nome, endereco)
                VALUES (?, ?) RETURNING id
                """, Long.class, "Local " + codigo, "Endereço de teste");

            Long evento = jdbc.queryForObject("""
                INSERT INTO evento
                (codigo, titulo, data_evento, capacidade,
                 capacidade_minima, vagas_disponiveis,
                 status, local_id)
                VALUES (?, ?, CURRENT_DATE, 1, 0, 1, 'ATIVO', ?)
                RETURNING id
                """, Long.class, "CONC-" + codigo.substring(0, 12),
                "Teste de concorrência", local);

            Long pessoaA = criarPessoa(
                "A", "52998224725", codigo);
            Long pessoaB = criarPessoa(
                "B", "11144477735", codigo);

            Long participanteA = jdbc.queryForObject("""
                INSERT INTO participante (pessoa_id)
                VALUES (?) RETURNING id
                """, Long.class, pessoaA);

            Long participanteB = jdbc.queryForObject("""
                INSERT INTO participante (pessoa_id)
                VALUES (?) RETURNING id
                """, Long.class, pessoaB);

            Long inscricaoA = criarInscricao(participanteA, evento);
            Long inscricaoB = criarInscricao(participanteB, evento);

            return new Dados(local, evento, pessoaA, pessoaB,
                participanteA, participanteB, inscricaoA, inscricaoB);
        });
    }

    private Long criarPessoa(String nome, String cpf, String codigo) {
        return jdbc.queryForObject("""
            INSERT INTO pessoa (nome, email, cpf)
            VALUES (?, ?, ?) RETURNING id
            """, Long.class, "Participante " + nome,
            nome.toLowerCase() + codigo + "@exemplo.com", cpf);
    }

    private Long criarInscricao(Long participante, Long evento) {
        return jdbc.queryForObject("""
            INSERT INTO inscricao (participante_id, evento_id, status)
            VALUES (?, ?, 'SOLICITADA') RETURNING id
            """, Long.class, participante, evento);
    }

    private void limpar(Dados d) {
        new TransactionTemplate(transacoes).executeWithoutResult(status -> {
            jdbc.update("DELETE FROM inscricao WHERE id IN (?, ?)",
                d.inscricaoA(), d.inscricaoB());
            jdbc.update("DELETE FROM participante WHERE id IN (?, ?)",
                d.participanteA(), d.participanteB());
            jdbc.update("DELETE FROM pessoa WHERE id IN (?, ?)",
                d.pessoaA(), d.pessoaB());
            jdbc.update("DELETE FROM evento WHERE id = ?", d.evento());
            jdbc.update("DELETE FROM local_evento WHERE id = ?", d.local());
        });
    }

    @Test
    void somenteUmaInscricaoPodeOcuparAUltimaVaga() throws Exception {
        Dados dados = preparar();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            Callable<String> tentativaA =
                () -> tentarConfirmar(dados.inscricaoA(), prontas, inicio);
            Callable<String> tentativaB =
                () -> tentarConfirmar(dados.inscricaoB(), prontas, inicio);

            Future<String> resultadoA = executor.submit(tentativaA);
            Future<String> resultadoB = executor.submit(tentativaB);

            assertTrue(prontas.await(10, TimeUnit.SECONDS),
                "As duas threads precisam estar prontas");

            inicio.countDown();

            List<String> resultados = List.of(
                resultadoA.get(20, TimeUnit.SECONDS),
                resultadoB.get(20, TimeUnit.SECONDS)
            );

            assertEquals(1, resultados.stream()
                .filter("CONFIRMADA"::equals).count());
            assertEquals(1, resultados.stream()
                .filter("SEM_VAGA"::equals).count());

            Integer vagas = jdbc.queryForObject(
                "SELECT vagas_disponiveis FROM evento WHERE id = ?",
                Integer.class, dados.evento());

            Integer confirmadas = jdbc.queryForObject("""
                SELECT COUNT(*) FROM inscricao
                WHERE evento_id = ? AND status = 'CONFIRMADA'
                """, Integer.class, dados.evento());

            assertEquals(0, vagas);
            assertEquals(1, confirmadas);
        } finally {
            inicio.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS),
                "As transações devem terminar antes da limpeza");
            limpar(dados);
        }
    }

    private String tentarConfirmar(
            Long id, CountDownLatch prontas,
            CountDownLatch inicio) throws Exception {

        prontas.countDown();
        assertTrue(inicio.await(10, TimeUnit.SECONDS));

        try {
            inscricoes.confirmar(id);
            return "CONFIRMADA";
        } catch (IllegalArgumentException erro) {
            if (!"Quantidade de vagas inválida".equals(erro.getMessage())) {
                throw erro;
            }
            return "SEM_VAGA";
        }
    }

    @Test
    void duasConfirmacoesDaMesmaInscricaoNaoPodemPassar() throws Exception {
        Dados dados = preparar();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            Callable<String> tarefa = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    inscricoes.confirmar(dados.inscricaoA());
                    return "CONFIRMADA";
                } catch (IllegalStateException e) {
                    assertEquals(
                        "Somente inscrições solicitadas podem ser confirmadas",
                        e.getMessage()
                    );
                    return "JA_PROCESSADA";
                }
            };

            Future<String> a = executor.submit(tarefa);
            Future<String> b = executor.submit(tarefa);

            assertTrue(prontas.await(10, TimeUnit.SECONDS));
            inicio.countDown();

            List<String> resultados = List.of(
                a.get(20, TimeUnit.SECONDS),
                b.get(20, TimeUnit.SECONDS)
            );

            assertEquals(1, resultados.stream()
                .filter("CONFIRMADA"::equals).count());

            assertEquals(1, resultados.stream()
                .filter("JA_PROCESSADA"::equals).count());

            Integer vagas = jdbc.queryForObject(
                "SELECT vagas_disponiveis FROM evento WHERE id = ?",
                Integer.class, dados.evento()
            );

            assertEquals(0, vagas);

        } finally {
            inicio.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(
                30, TimeUnit.SECONDS
            ));
            limpar(dados);
        }
    }


    @Test
    void doisCancelamentosDaMesmaInscricaoNaoDevolvemDuasVagas()
            throws Exception {

        Dados dados = preparar();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            inscricoes.confirmar(dados.inscricaoA());

            Callable<String> tarefa = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    inscricoes.cancelar(dados.inscricaoA());
                    return "CANCELADA";
                } catch (IllegalStateException e) {
                    assertEquals("Inscrição já cancelada", e.getMessage());
                    return "JA_CANCELADA";
                }
            };

            Future<String> a = executor.submit(tarefa);
            Future<String> b = executor.submit(tarefa);

            assertTrue(prontas.await(10, TimeUnit.SECONDS));
            inicio.countDown();

            List<String> resultados = List.of(
                a.get(20, TimeUnit.SECONDS),
                b.get(20, TimeUnit.SECONDS)
            );

            assertEquals(1, resultados.stream()
                .filter("CANCELADA"::equals).count());

            assertEquals(1, resultados.stream()
                .filter("JA_CANCELADA"::equals).count());

            Integer vagas = jdbc.queryForObject(
                "SELECT vagas_disponiveis FROM evento WHERE id = ?",
                Integer.class, dados.evento()
            );

            String situacao = jdbc.queryForObject(
                "SELECT status FROM inscricao WHERE id = ?",
                String.class, dados.inscricaoA()
            );

            assertEquals(1, vagas);
            assertEquals("CANCELADA", situacao);

        } finally {
            inicio.countDown();
            executor.shutdownNow();

            assertTrue(executor.awaitTermination(
                30, TimeUnit.SECONDS
            ));

            limpar(dados);
        }
    }


    @Test
    void reservaManualEInscricaoDisputamUltimaVaga() throws Exception {
        Dados dados = preparar();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            Callable<String> confirmacao = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    inscricoes.confirmar(dados.inscricaoA());
                    return "INSCRICAO";
                } catch (IllegalArgumentException erro) {
                    assertEquals(
                        "Quantidade de vagas inválida",
                        erro.getMessage()
                    );
                    return "SEM_VAGA";
                }
            };

            Callable<String> reservaManual = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    eventoService.reservarVagas(dados.evento(), 1);
                    return "RESERVA_MANUAL";
                } catch (IllegalArgumentException erro) {
                    assertEquals(
                        "Quantidade de vagas inválida",
                        erro.getMessage()
                    );
                    return "SEM_VAGA";
                }
            };

            Future<String> a = executor.submit(confirmacao);
            Future<String> b = executor.submit(reservaManual);

            assertTrue(prontas.await(10, TimeUnit.SECONDS));
            inicio.countDown();

            String resultadoA = a.get(20, TimeUnit.SECONDS);
            String resultadoB = b.get(20, TimeUnit.SECONDS);

            assertEquals(1, List.of(resultadoA, resultadoB).stream()
                .filter("SEM_VAGA"::equals).count());

            Integer vagas = jdbc.queryForObject(
                "SELECT vagas_disponiveis FROM evento WHERE id = ?",
                Integer.class, dados.evento()
            );

            Integer confirmadas = jdbc.queryForObject(
                "SELECT COUNT(*) FROM inscricao WHERE evento_id = ? AND status = 'CONFIRMADA'",
                Integer.class, dados.evento()
            );

            assertEquals(0, vagas);
            assertEquals(
                "INSCRICAO".equals(resultadoA) ? 1 : 0,
                confirmadas
            );

        } finally {
            inicio.countDown();
            executor.shutdownNow();

            assertTrue(executor.awaitTermination(
                30, TimeUnit.SECONDS
            ));

            limpar(dados);
        }
    }


    @Test
    void alteracaoCapacidadeEReservaManualNaoPerdemAtualizacao()
            throws Exception {

        Dados dados = preparar();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch prontas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            Callable<String> alterar = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    eventoService.alterar(
                        dados.evento(),
                        "Evento atualizado",
                        java.time.LocalDate.now(),
                        0,
                        0,
                        dados.local(),
                        null
                    );
                    return "ALTERADA";
                } catch (IllegalArgumentException e) {
                    return "RECUSADA";
                }
            };

            Callable<String> reservar = () -> {
                prontas.countDown();
                assertTrue(inicio.await(10, TimeUnit.SECONDS));

                try {
                    eventoService.reservarVagas(dados.evento(), 1);
                    return "RESERVADA";
                } catch (IllegalArgumentException e) {
                    return "RECUSADA";
                }
            };

            Future<String> a = executor.submit(alterar);
            Future<String> b = executor.submit(reservar);

            assertTrue(prontas.await(10, TimeUnit.SECONDS));
            inicio.countDown();

            String resultadoA = a.get(20, TimeUnit.SECONDS);
            String resultadoB = b.get(20, TimeUnit.SECONDS);

            assertEquals(1, List.of(resultadoA, resultadoB).stream()
                .filter("RECUSADA"::equals).count());

            java.util.Map<String, Object> evento = jdbc.queryForMap(
                "SELECT capacidade, vagas_disponiveis FROM evento WHERE id = ?",
                dados.evento()
            );

            int capacidade = ((Number) evento.get("capacidade")).intValue();
            int vagas = ((Number) evento.get("vagas_disponiveis")).intValue();

            assertEquals(0, vagas);

            if ("ALTERADA".equals(resultadoA)) {
                assertEquals("RECUSADA", resultadoB);
                assertEquals(0, capacidade);
            } else {
                assertEquals("RESERVADA", resultadoB);
                assertEquals(1, capacidade);
            }

        } finally {
            inicio.countDown();
            executor.shutdownNow();

            assertTrue(executor.awaitTermination(
                30, TimeUnit.SECONDS
            ));

            limpar(dados);
        }
    }

}
