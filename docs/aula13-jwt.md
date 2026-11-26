# Aula 13 — JWT, Bearer Token e OAuth2 Resource Server

## 1. Objetivo

Substituir a autenticacao HTTP Basic da Aula 12 por JWT,
evitando o reenvio de login e senha em cada requisicao protegida.

Projeto: Eventos 2026.
Referencia: professor Jefferson Arpasserini, Aula 13.

## 2. Funcionamento

1. O cliente envia login e senha para POST /api/autenticacao/login.
2. O AuthenticationManager valida as credenciais e o estado da conta.
3. O TokenService emite um JWT assinado com HS256.
4. O cliente recebe accessToken, tokenType e dados de expiracao.
5. As proximas requisicoes utilizam Authorization: Bearer.
6. O JwtDecoder valida assinatura, emissor e validade do token.

A autenticacao e stateless: nao ha sessao HTTP armazenada
para identificar o usuario entre as requisicoes.

## 3. Estrutura do JWT

O JWT assinado contem tres partes:

header.payload.signature

O header identifica o algoritmo HS256.

O payload possui:

- iss: https://eventos2026.local
- sub: login autenticado
- iat: instante de emissao
- exp: instante de expiracao
- jti: identificador unico do token
- perfis: papeis provenientes da conta autenticada

A assinatura HMAC-SHA256 protege a integridade e autenticidade.

JWT assinado nao e criptografado. Header e payload podem ser
decodificados, portanto nao devem conter dados confidenciais.

## 4. Chave e configuracao

- A chave HS256 possui pelo menos 256 bits.
- O segredo real fica na variavel JWT_SECRET do arquivo .env local.
- O .env nao pode ser publicado no Git.
- O .env.example contem somente instrucoes e valores ficticios.
- A expiracao configurada e PT30M, equivalente a 30 minutos.

O mesmo segredo e utilizado para emitir e validar tokens HS256.
Em arquiteturas com emissores separados, RS256 e uma alternativa.

## 5. Endpoints

POST /api/autenticacao/login: publico, valida credenciais.
GET /api/contas-acesso/me: exige Bearer Token.
PUT /api/contas-acesso/me/senha: exige Bearer Token.
GET /api/eventos: exige Bearer Token.
GET /api/health: publico.

O HTTP Basic nao permite mais acessar as rotas protegidas.

## 6. Testes realizados

Em 26/11/2026, a suite Java apresentou:

- 63 testes executados.
- 0 falhas.
- 0 erros.
- BUILD SUCCESS.

Foram verificados:

- Emissao e decodificacao de um JWT valido.
- Emissor, sujeito, perfil, identificador e expiracao.
- Rejeicao de assinatura adulterada.
- Rejeicao de token expirado.
- Rejeicao de credenciais incorretas e contas bloqueadas.
- Troca de senha e impossibilidade de novo login com a senha antiga.

Validacao HTTP externa:

- Health sem token: 200.
- Eventos sem token: 401.
- Eventos com Bearer valido: 200.
- HTTP Basic com credenciais corretas: 401.

No Postman, o Collection Runner executou quatro requisicoes
sem falhas nem erros de execucao, utilizando captura automatica
da variavel accessToken.

Colecao: postman/Eventos-2026-Aula-13.postman_collection.json.

## 7. Limitacoes e decisoes

A validade de 30 minutos e absoluta, nao por inatividade.
Usar o token nao renova automaticamente sua expiracao.

O bloqueio de uma conta ou a alteracao de senha impede novos
logins, mas nao invalida automaticamente JWTs ja emitidos.

Nao foram implementados refresh tokens, logout com revogacao,
introspeccao ou controle de permissoes por perfil.

Uma estrategia futura para Eventos 2026 seria manter uma
lista de revogacao por jti em armazenamento compartilhado,
com expiracao dos registros no prazo restante do token.

Outra alternativa seria utilizar versao de credenciais e
consultar esse estado durante a validacao, com o custo de
reduzir a independencia da autenticacao stateless.

Em producao, devem ser considerados HTTPS, rate limiting,
protecao de tokens, rotacao de chaves e auditoria.

## 8. Proximos passos

Na Aula 14, implementar autorizacao por perfis e analisar
as permissoes de OPERADOR e ADMINISTRADOR.
