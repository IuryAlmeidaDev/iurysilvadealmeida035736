# Projeto Prático - Implementação Back End Java Sênior

> **Candidato:** Iury Almeida  
> **Cargo:** Analista de Tecnologia da Informação  
> **Perfil:** Engenheiro da Computação (Nível Sênior)  
> **Processo Seletivo:** Edital Conjunto N° 001/2026/SEPLAG/SEFAZ/SEDUC...

---

API desenvolvida em **Spring Boot** para gerenciamento de artistas e álbuns musicais, atendendo aos requisitos do **Anexo II-A** do edital. O projeto conta com autenticação JWT, versionamento de endpoints, upload de imagens em armazenamento S3 compatível (MinIO) e controle de banco via Flyway.

Todos os endpoints estão versionados em `/v1`.

---

## 🧱 Stack Tecnológica

- Java 17
- Spring Boot
- Spring Security (JWT – access + refresh token)
- PostgreSQL 16
- Flyway (migrations e carga inicial)
- MinIO (S3 compatível)
- OpenAPI / Swagger
- Spring Actuator (health, readiness, liveness)
- Docker + Docker Compose

---

## ▶️ Como executar com Docker (recomendado)

### Subir todos os serviços
```bash
docker compose up --build
```

O comando acima sobe:
- API Spring Boot
- PostgreSQL
- MinIO
- Criação automática do bucket `capas-albuns`

---

## 🌐 URLs importantes

- **Swagger (OpenAPI)**  
  http://localhost:8080/swagger-ui/index.html

- **Health Check**  
  http://localhost:8080/actuator/health

- **Readiness**  
  http://localhost:8080/actuator/health/readiness

- **Liveness**  
  http://localhost:8080/actuator/health/liveness

- **MinIO Console**  
  http://localhost:9001  
  Usuário: `minioadmin`  
  Senha: `minioadmin`

- **MinIO API (S3)**  
  http://localhost:9000

---

## 🔐 Autenticação (JWT)

A API utiliza **JWT** para autenticação:
- Access Token: expira em **5 minutos**
- Refresh Token: expiração configurada separadamente

### Login
`POST /v1/auth/login`

### Refresh Token
`POST /v1/auth/refresh`

> ⚠️ Observação: o usuário inicial é criado via **Flyway migration**.

---

## 📚 Endpoints principais

### 🎤 Artistas
- `GET /v1/artistas`
- `POST /v1/artistas`
- `PUT /v1/artistas/{id}`
- `GET /v1/artistas/{id}`

### 💿 Álbuns
- `GET /v1/albuns`
- `POST /v1/albuns`
- `PUT /v1/albuns/{id}`
- `GET /v1/albuns/{id}`
- `GET /v1/albuns?tipoArtista=CANTOR|BANDA`

### 🖼️ Upload de imagens
- `POST /v1/albuns/{id}/capa`
- `POST /v1/albuns/{id}/imagens`

---

## 🧪 Estratégia de Testes Automatizados

O projeto implementa uma pirâmide de testes robusta, cobrindo desde a lógica de negócio isolada até a integração completa com a infraestrutura de banco de dados e cache, garantindo a resiliência exigida pelo edital.

### 🏗️ Tipos de Testes Implementados

* **Unitários:** Validação das regras de negócio nas camadas de `Service` utilizando **JUnit 5** e **Mockito** para isolamento total de dependências.
* **Controller (Slicing):** Testes de contrato e comportamento utilizando `@WebMvcTest`. Validam o mapeamento de rotas, payloads JSON, validações de Bean Validation e filtros de segurança (JWT).
* **Integração (Full Context):** Testes ponta-a-ponta utilizando `@SpringBootTest` com ambiente real de memória para validar a persistência JPA e o fluxo de segurança completo.

### 🚀 Diferenciais de Infraestrutura (Portabilidade)

Para garantir que a suíte de testes seja executada em qualquer ambiente sem necessidade de configurações manuais ou dependência de Docker, foram adotadas as seguintes tecnologias:

* **Redis em Memória:** Utilização do `embedded-redis`, que é iniciado automaticamente durante os testes de integração para validar o **Rate Limit Distribuído** de forma isolada e veloz.
* **Banco de Dados H2:** Persistência testada em memória com `MODE=PostgreSQL`, garantindo que as migrations do **Flyway** sejam validadas em cada build.
* **Validação de Rate Limit:** Teste automatizado dedicado que simula o consumo de tokens e confirma o bloqueio preventivo (HTTP 429) após exceder o limite de requisições.



### 🛠️ Como Executar os Testes

**Executar toda a suíte de testes:**
```bash
mvn test
```
# Executar apenas os testes de integração (Infraestrutura)
mvn -Dtest=*IntegrationTest test

---

## 🧪 Observações Técnicas

- Banco gerenciado exclusivamente pelo Flyway
- Hibernate configurado com `ddl-auto=validate`
- Profile `docker` usado no ambiente containerizado
- Buckets MinIO criados automaticamente
- Endpoints protegidos por JWT
- Actuator habilitado

---

## 🗂️ Estrutura do Projeto

- `controller/`
- `service/`
- `repository/`
- `config/`
- `model/`
- `db/migration/`
