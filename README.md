# Projeto Prático - Implementação Back End Java Sênior

> **Candidato:** Iury Almeida  
> **Cargo:** Analista de Tecnologia da Informação  
> **Perfil:** Engenheiro da Computação (Nível Sênior)  
> **Processo Seletivo:** Edital Conjunto Nº 001/2026/SEPLAG/SEFAZ/SEDUC  

---

## 1. Visão Geral do Projeto

Este projeto consiste na implementação de uma **API RESTful Back End em Java**, desenvolvida com **Spring Boot**, cujo objetivo é gerenciar um **catálogo musical** composto por **Artistas** e **Álbuns**, atendendo integralmente aos requisitos técnicos e arquiteturais definidos no edital do processo seletivo.

A solução foi construída com foco em:
- boas práticas de engenharia de software
- separação clara de responsabilidades
- segurança, escalabilidade e observabilidade
- aderência a padrões utilizados em ambientes reais de produção

Toda a aplicação pode ser executada de forma **isolada e reprodutível** por meio de **Docker Compose**, eliminando dependências externas para avaliação.

---

## 2. Stack Tecnológica

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **JWT (Access Token + Refresh Token)**
- **PostgreSQL**
- **Flyway** (controle de versionamento do banco)
- **Redis**
- **Bucket4j** (rate limit distribuído)
- **MinIO** (storage S3 compatível)
- **WebSocket (STOMP)**
- **Spring Actuator**
- **Docker / Docker Compose**
- **Maven**

---

## 3. Arquitetura da Solução

A aplicação segue uma arquitetura em camadas, amplamente adotada em projetos corporativos:

- **Controller:** exposição dos endpoints REST, validação de entrada e versionamento de API.
- **Service:** regras de negócio, orquestração de fluxos, integrações e controle transacional.
- **Repository:** persistência de dados utilizando Spring Data JPA.
- **Model:** entidades JPA, enums e relacionamentos.
- **Config:** configurações de segurança, CORS, WebSocket, rate limit e integrações externas.
- **db/migration:** migrations Flyway para criação e evolução do schema do banco.

Essa abordagem garante manutenibilidade, testabilidade e facilidade de evolução do sistema.

---

## 4. Atendimento aos Requisitos do Edital

Esta seção apresenta, de forma objetiva, como cada requisito solicitado no edital foi atendido.

### 4.1 API REST para gerenciamento de dados
✔ **Atendido**
- Endpoints RESTful versionados em `/v1`
- Operações completas de CRUD para Artistas e Álbuns
- Paginação e filtros utilizando `Pageable`

### 4.2 Persistência em banco de dados relacional
✔ **Atendido**
- Banco de dados PostgreSQL
- Controle de schema e versionamento via Flyway
- Migrations automatizadas no startup da aplicação

### 4.3 Autenticação e autorização
✔ **Atendido**
- Autenticação baseada em JWT
- Emissão de Access Token e Refresh Token
- Proteção de endpoints sensíveis com Spring Security
- Controle de acesso por perfil (roles)

### 4.4 Criação e gerenciamento de usuários
✔ **Atendido**

> ⚠️ **Observação Importante**  
> Não existe usuário pré-criado via script ou migration.  
> O primeiro usuário deve ser criado utilizando o endpoint:
> `POST /v1/auth/register`  
>
> Essa decisão segue práticas reais de mercado, evitando credenciais fixas e simulando um ambiente de produção seguro.

### 4.5 Upload e gerenciamento de arquivos
✔ **Atendido**
- Upload multipart de capa do álbum e imagens adicionais
- Armazenamento em serviço S3 compatível (MinIO)
- Retorno de URLs pré-assinadas para acesso controlado

### 4.6 Rate Limit
✔ **Atendido**
- Implementação de rate limit distribuído
- Redis como storage compartilhado
- Controle por usuário autenticado ou endereço IP
- Retorno HTTP 429 quando o limite é excedido

### 4.7 Comunicação em tempo real
✔ **Atendido**
- Implementação de WebSocket com protocolo STOMP
- Publicação automática de evento ao criar novo álbum
- Tópico: `/topic/new-album`

---

## 5. Funcionalidades Implementadas

### Artistas
- Cadastro
- Atualização
- Remoção
- Listagem paginada
- Busca por texto

### Álbuns
- Cadastro vinculado a artista
- Atualização
- Remoção
- Listagem paginada
- Filtro por tipo de artista

### Segurança
- Registro de usuários
- Login
- Renovação de token (refresh)
- Autorização por role

---

## 6. Execução do Projeto

### 6.1 Execução com Docker Compose (Recomendado)

Pré-requisitos:
- Docker
- Docker Compose

```bash
docker compose up --build
```

Serviços disponíveis:
- API: http://localhost:8080
- PostgreSQL: localhost:5432
- Redis: localhost:6379
- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001  
  Usuário/Senha padrão: minioadmin / minioadmin

---

## 7. Fluxo Rápido de Validação (Avaliador)

1. Subir a aplicação com Docker Compose  
2. Criar usuário em `/v1/auth/register`  
3. Realizar login em `/v1/auth/login`  
4. Criar artista (endpoint protegido)  
5. Criar álbum (endpoint protegido)  
6. Validar recebimento do evento WebSocket em `/topic/new-album`  

Todo o fluxo pode ser validado em menos de 5 minutos.

---

## 8. Endpoints Principais

### Autenticação
- POST `/v1/auth/register`
- POST `/v1/auth/login`
- POST `/v1/auth/refresh`

### Artistas
- GET `/v1/artistas`
- GET `/v1/artistas/{id}`
- POST `/v1/artistas`
- PUT `/v1/artistas/{id}`
- DELETE `/v1/artistas/{id}`

### Álbuns
- GET `/v1/albuns`
- GET `/v1/albuns/{id}`
- POST `/v1/albuns`
- PUT `/v1/albuns/{id}`
- DELETE `/v1/albuns/{id}`
- POST `/v1/albuns/{id}/capa`
- POST `/v1/albuns/{id}/imagens`

---

## 9. Observabilidade

A aplicação expõe endpoints do Spring Actuator para monitoramento:

- `/actuator/health`
- `/actuator/health/readiness`
- `/actuator/health/liveness`

---

## 10. Testes

Os testes podem ser executados com:

```bash
./mvnw test
```

Incluem:
- testes unitários de serviços
- testes de controllers
- testes de integração

---

## 11. Considerações Finais

Este projeto foi desenvolvido com foco em qualidade, clareza e aderência ao edital, simulando um cenário real de aplicação corporativa em produção.

A documentação foi estruturada para permitir **avaliação técnica objetiva**, **reprodutibilidade do ambiente** e **rastreabilidade completa dos requisitos**.

