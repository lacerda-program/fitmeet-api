# 🏃 FitMeet API

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-blue?style=for-the-badge&logo=docker)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-documented-85EA2D?style=for-the-badge&logo=swagger)

> API REST da plataforma **FitMeet** — Encontre parceiros para atividades esportivas ao ar livre.

---

## 📋 Sobre o Projeto

O FitMeet é uma plataforma que permite o cadastro de usuários e a criação, inscrição e gerenciamento de atividades esportivas. Os usuários podem selecionar interesses, participar de atividades ou criá-las e acompanhar seu progresso com XP, níveis e conquistas.

---

## 🚀 Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 4.0.6**
- **Spring Security + JWT**
- **Spring Data JPA + Hibernate**
- **PostgreSQL 16**
- **Liquibase** — versionamento do banco de dados
- **LocalStack (S3)** — armazenamento de imagens
- **SpringDoc OpenAPI (Swagger)** — documentação da API
- **Docker + Docker Compose**
- **JUnit 5 + Mockito** — testes unitários
- **Lombok**
- **Gradle**

---

## ⚙️ Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Java 25+](https://adoptium.net/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/)

---

## 🐳 Executando com Docker

### 1. Clone o repositório

```bash
git clone https://github.com/bc-fullstack-07/Samuel-Lacerda-de-Sousa.git
cd Samuel-Lacerda-de-Sousa/Back-end
```

### 2. Suba os containers

```bash
docker-compose up -d
```

Isso irá subir:
- ✅ **PostgreSQL** na porta `5432`
- ✅ **LocalStack S3** na porta `4566`
- ✅ **API** na porta `8080`

### 3. Acesse a aplicação

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |

---

## 💻 Executando Localmente (sem Docker para a API)

### 1. Suba apenas o banco e o LocalStack

```bash
docker-compose up postgres localstack -d
```

### 2. Execute a aplicação

```bash
# Mac/Linux
./gradlew bootRun

# Windows
.\gradlew.bat bootRun
```

---

## 🔐 Autenticação

A API utiliza **JWT Bearer Token**. Para acessar endpoints protegidos:

1. Cadastre um usuário em `POST /auth/register`
2. Faça login em `POST /auth/sign-in`
3. Copie o token retornado
4. No Swagger, clique em **Authorize** e cole o token
5. Para requisições manuais, adicione o header:
```
Authorization: Bearer {seu-token}
```

---

## 📚 Endpoints

### 🔓 Autenticação (públicos)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/register` | Cadastro de usuário |
| POST | `/auth/sign-in` | Login de usuário |

### 👤 Usuários (protegidos)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/user` | Buscar dados do usuário |
| GET | `/user/preferences` | Buscar interesses do usuário |
| POST | `/user/preferences/define` | Definir interesses do usuário |
| PUT | `/user/avatar` | Editar foto de perfil |
| PUT | `/user/update` | Editar dados do usuário |
| DELETE | `/user/deactivate` | Desativar conta |

### 🏃 Atividades (protegidos)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/activities/types` | Listar tipos de atividades |
| GET | `/activities` | Listar atividades com paginação |
| GET | `/activities/all` | Listar todas as atividades |
| GET | `/activities/user/creator` | Atividades criadas pelo usuário |
| GET | `/activities/user/creator/all` | Todas as atividades criadas |
| GET | `/activities/user/participant` | Atividades em que está inscrito |
| GET | `/activities/user/participant/all` | Todas as atividades inscritas |
| GET | `/activities/{id}/participants` | Participantes de uma atividade |
| POST | `/activities/new` | Criar uma atividade |
| POST | `/activities/{id}/subscribe` | Inscrever-se em uma atividade |
| PUT | `/activities/{id}/update` | Editar uma atividade |
| PUT | `/activities/{id}/conclude` | Concluir uma atividade |
| PUT | `/activities/{id}/approve` | Aprovar/negar participante |
| PUT | `/activities/{id}/check-in` | Fazer check-in |
| DELETE | `/activities/{id}/unsubscribe` | Cancelar inscrição |
| DELETE | `/activities/{id}/delete` | Excluir atividade |

---

## 🎮 Sistema de XP e Níveis

- A cada **check-in** confirmado, tanto o participante quanto o criador recebem **50 XP**
- A cada **100 XP** acumulados, o usuário sobe **1 nível**

---

## 🏆 Conquistas

| Conquista | Critério |
|-----------|----------|
| 🥇 Primeiro Check-in | Confirmou presença em uma atividade pela primeira vez |
| 🎯 Criador de Atividades | Criou uma atividade pela primeira vez |
| 🏁 Atividade Concluída | Concluiu uma atividade pela primeira vez |
| ⬆️ Subiu de Nível | Subiu de nível pela primeira vez |
| 📸 Nova Foto | Alterou a foto de perfil pela primeira vez |

---

## 🗄️ Banco de Dados

### Configuração padrão

| Propriedade | Valor |
|-------------|-------|
| Host | localhost |
| Porta | 5432 |
| Banco | bootcamp_db |
| Usuário | postgres |
| Senha | postgres |

### Tabelas

```
users                  → Usuários da plataforma
activity_types         → Tipos de atividades (Futebol, Basquete, etc.)
activities             → Atividades criadas pelos usuários
activity_addresses     → Endereços (coordenadas GPS) das atividades
activity_participants  → Inscrições dos usuários nas atividades
achievements           → Definição das conquistas
user_achievements      → Conquistas dos usuários
preferences            → Interesses dos usuários
```

---

## ☁️ Armazenamento de Imagens

As imagens são armazenadas no **LocalStack S3** (simulação local da AWS S3).

| Propriedade | Valor |
|-------------|-------|
| Endpoint | http://localhost:4566 |
| Bucket | bootcamp-bucket |
| Region | us-east-1 |
| Access Key | test |
| Secret Key | test |

---

## 🧪 Testes

Para executar os testes unitários:

```bash
# Mac/Linux
./gradlew test

# Windows
.\gradlew.bat test
```

Os testes cobrem as camadas de **Service** e **Controller**:

```
service/
  ├── AuthServiceTest
  ├── UserServiceTest
  └── ActivityServiceTest

controller/
  ├── AuthControllerTest
  ├── UserControllerTest
  └── ActivityControllerTest
```

---

## 📁 Estrutura do Projeto

```
src/main/java/bootcamp07/api/
├── config/         → Configurações (Security, Swagger, LocalStack)
├── controller/     → Controllers REST
├── dto/
│   ├── request/    → DTOs de entrada
│   └── response/   → DTOs de saída
├── exception/      → Tratamento de exceções
├── model/          → Entidades JPA
├── repository/     → Interfaces Spring Data
├── security/       → JWT e filtros de segurança
└── service/        → Regras de negócio
```

---

## 👨‍💻 Autor

**Samuel Lacerda de Sousa**

Desenvolvido durante o **Bootcamp Fullstack 2026 — SysMap Solutions**

---

## 📄 Licença

Este projeto está sob a licença MIT.
