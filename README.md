# Eventos 2026 — Aulas 06 a 09

Aplicação de eventos com Java 21, Spring Boot 4.1.1, JPA, PostgreSQL, Liquibase, API REST, DTOs, serviços, mapeadores e testes MockMvc.

## O que foi implementado

- Entidades `LocalEvento`, `Organizador` e `Evento`. Um evento exige local, mas seu organizador é opcional.
- A evolução do domínio adiciona `Organizador` e `capacidadeMinima` (obrigatória).
- Migrações 001 (base), 002 (evento legado) e 003 (expand → backfill → constrain) com nomes de constraints e rollback estruturais.
- API de leitura e cadastro (`POST`, `GET`, `GET /{id}`) para os três recursos, com DTOs/validações e erros uniformes.
- Testes MockMvc com sucesso, validação, recurso inexistente, duplicidade e JSON inválido.
- Coleção Postman com variáveis e testes.

## Como executar

1. Instale JDK 21 e PostgreSQL. Crie usuário `eventos_app` e banco `eventos2026_dev` com acesso de criação de tabelas.
2. Copie `.env.example` para `.env` e preencha a senha. **Não publique `.env`.**
3. No diretório do projeto, rode `./mvnw test` (H2 em memória no perfil de testes) e `./mvnw spring-boot:run` (PostgreSQL).
4. Acesse `http://localhost:8080/api/health` e importe `postman/Eventos-2026-Aula-07.postman_collection.json`.

O banco de produção/desenvolvimento não é criado pelo aplicativo. O Liquibase aplica 001 → 002 → 003 automaticamente ao iniciar. A linha `LEGADO-001` demonstra o backfill: após 003, conserva os dados e recebe `capacidade_minima=0`.

## Rotas

| Recurso | Cadastro | Listagem | Consulta |
|---|---|---|---|
| Locais | `POST /api/locais` | `GET /api/locais` | `GET /api/locais/{id}` |
| Organizadores | `POST /api/organizadores` | `GET /api/organizadores` | `GET /api/organizadores/{id}` |
| Eventos | `POST /api/eventos` | `GET /api/eventos` | `GET /api/eventos/{id}` |

`POST` responde `201` com `Location` porque cria um recurso; `GET` responde `200` para consulta bem-sucedida; `400` marca dados inválidos, `404` marca recurso ausente e `409` marca conflito de unicidade. Não há `PUT`, `PATCH` e `DELETE` porque pertencem à Aula 08.

### Exemplo de cadastro

Primeiro crie um local (`POST /api/locais`): `{ "nome": "Ginásio Municipal", "endereco": "Centro" }`. Depois um organizador opcional (`POST /api/organizadores`): `{ "nome": "Atlética", "email": "contato@example.com" }`.

`POST /api/eventos`:

```json
{"codigo":"EVENTO-001","titulo":"Campeonato de jogos","dataEvento":"2026-11-20","capacidade":200,"capacidadeMinima":30,"localId":1,"organizadorId":1}
```

## Aula 06: exercício de diff (operação gerada → revisão)

| Operação gerada | Risco | Decisão final | Justificativa | Teste |
|---|---|---|---|---|
| Remove e recria FK existente | Interromper relações | Não aceitar ruído | `RESTRICT` pode diferir de `NO ACTION` | Comparar constraint original |
| `capacidade_minima NOT NULL` direto | Falhar sobre dados antigos | Expandir, preencher zero, restringir | Preserva `LEGADO-001` | `listaEventoLegadoPreservado` |
| Constraint com nome aleatório | Drift futuro | Nome estável `ck_evento_capacidade_minima` | Revisões reproduzíveis | Inspecionar esquema |
| CHECK omitido | Valores inconsistentes | Adicionar checks | Mínimo entre zero e capacidade | Teste SQL de violação |
| Rollback ausente | Reversão incerta | Rollbacks de esquema | Não apagar dados sem decisão explícita | `liquibase:rollback` em banco descartável |

O diff automático não conhece registros existentes, intenção de negócio nem segurança de rollback. `003` é o changelog oficial revisado. O backfill é deliberadamente não reversível: transformou dados antigos de forma semanticamente válida, e desfazê-lo exigiria decidir o que significa nulo.

### Gerar diff de referência (somente bancos descartáveis)

1. Crie `eventos2026_diff` e `eventos2026_reference` vazios, diferentes de `dev/test`.
2. Carregue variáveis `.env` no processo do shell: `set -a; source .env; set +a`.
3. Aplique a baseline em `diff`: `./mvnw -Dliquibase.changelog.file=src/test/resources/db/changelog/db.changelog-baseline.yaml liquibase:update`.
4. Crie o esquema das entidades em `reference`: `./mvnw spring-boot:run -Dspring-boot.run.profiles=schema-reference`.
5. Rode `./mvnw liquibase:diff` e inspecione o rascunho em `target/liquibase-diff/changelog-gerado.yaml`.
6. Compare o rascunho com 003, depois aplique 003 ao banco descartável e repita o diff para investigar divergências remanescentes.

**Atenção:** `schema-reference` usa `ddl-auto=create` e apaga/recria tabelas! Jamais aponte esse perfil para banco com dados relevantes.

## Limites da entrega

O ZIP contém o código e os testes, mas os resultados de `liquibase:diff` com dois PostgreSQL reais, a conferência visual do Postman e o histórico real do banco precisam ser executados na máquina com PostgreSQL configurado. Não inclua `.env`, logs ou `target/` no Git.
# Continuação da Aula 08 — Eventos 2026

Esta versão adapta o conteúdo da aula de CRUD, estoque e paginação para o domínio de eventos.

## Novas rotas

| Método | Rota | Finalidade |
| --- | --- | --- |
| PUT | `/api/eventos/{id}` | Alterar dados editáveis, preservando o código |
| DELETE | `/api/eventos/{id}` | Excluir evento |
| PUT | `/api/eventos/{id}/status` | Alterar status (`ATIVO`/`INATIVO`) |
| POST | `/api/eventos/{id}/vagas/reservas` | Reservar quantidade de vagas |
| POST | `/api/eventos/{id}/vagas/liberacoes` | Liberar quantidade de vagas já reservadas |
| GET | `/api/eventos` | Pesquisar com filtros, ordenação e paginação |
| PUT | `/api/locais/{id}` | Alterar nome e endereço |
| DELETE | `/api/locais/{id}` | Excluir local apenas quando não tem eventos vinculados |

Filtros de eventos: `titulo`, `status`, `localId`, `organizadorId`, `dataInicial`, `dataFinal`, `vagasDisponiveis`.
Exemplo: `/api/eventos?titulo=Feira&status=ATIVO&page=0&size=10&sort=dataEvento,desc`.
As listagens de eventos agora devolvem `PaginaResponse` (`conteudo`, `pagina`, `tamanho`, `totalElementos`, `totalPaginas`, `primeira`, `ultima`), não mais um array na raiz.

**Banco:** não altere as migrações 001–003 já aplicadas. A migração 004 adiciona `vagas_disponiveis` e inicializa cada evento existente com a capacidade total.

**Testes:** rode `./mvnw test` no Mac, dentro da pasta do projeto, antes de usar o Postman. As dependências não puderam ser baixadas no ambiente de preparação, portanto a compilação e os testes desta continuação precisam ser confirmados no Mac.

**Credenciais:** o `.env` não acompanha este pacote. Preserve o `.env` local da sua instalação e nunca o publique no GitHub.

---

## Aula 08 — CRUD e controle de vagas

- CRUD de eventos e locais.
- Filtros, ordenação e paginação.
- Reserva e liberação de vagas.
- Ativação e inativação de eventos.
- Validações e tratamento de erros HTTP.

## Aula 09 — Swagger e OpenAPI

- Integração com SpringDoc OpenAPI.
- Interface Swagger UI.
- Documentação dos endpoints e respostas HTTP.
- Organização das rotas por grupos.
- Configuração para desabilitar o Swagger em produção.
- Testes automatizados da documentação.

### Acessar a documentação

Com a aplicação em execução:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

A documentação fica habilitada no ambiente de desenvolvimento.

## Aula 11 — Concorrência de vagas e locking

A Aula 11 adapta os conceitos de concorrência de estoque do
material do professor Jefferson Arpasserini para o gerenciamento
de vagas do Eventos 2026.

### Implementação

- Transações gerenciadas com `@Transactional`.
- Bloqueio pessimista de escrita com `PESSIMISTIC_WRITE`.
- Uso do `EntityManager.refresh()` para obter o estado atualizado
  das entidades sob bloqueio.
- Proteção de confirmações e cancelamentos de inscrições.
- Proteção de reservas e liberações manuais de vagas.
- Coordenação de alterações de capacidade, status e exclusão
  de eventos.
- Sincronização de alterações pendentes com `flush()` quando
  necessário.

### Testes de concorrência com PostgreSQL

Os testes utilizam o banco isolado `eventos2026_test`, com
perfil `testpg`, sem utilizar os dados do banco de desenvolvimento.

Cenários implementados:

1. Duas inscrições disputando a última vaga.
2. Duas confirmações da mesma inscrição.
3. Dois cancelamentos da mesma inscrição.
4. Reserva manual concorrendo com confirmação de inscrição.
5. Alteração de capacidade concorrendo com reserva manual.

Executar os testes:

```bash
./mvnw test -Dtest=InscricaoConcorrenciaTest
```

Executar a suíte completa:

```bash
./mvnw clean test
```

O perfil PostgreSQL exige uma instância local acessível, um banco
de testes próprio e autenticação configurada para o ambiente.
A configuração local sem senha não deve ser reproduzida
automaticamente em produção.

### Limitações e cuidados

- Os testes verificam cenários específicos, não todas as
  intercalações possíveis entre transações.
- Bloqueios devem permanecer dentro de transações curtas.
- O bloqueio pessimista pode produzir espera e deadlocks;
  operações não devem aplicar tentativas automáticas
  indiscriminadas.
- As operações que alteram vagas precisam continuar adotando
  a mesma política de coordenação.
- Não houve necessidade de adicionar uma nova migração Liquibase
  apenas para implementar os bloqueios.
