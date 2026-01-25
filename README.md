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
