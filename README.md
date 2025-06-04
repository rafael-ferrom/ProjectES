# 🩺 Sistema de Gerenciamento de Medicamentos

Este é um projeto de uma API REST desenvolvida com **Spring Boot** e banco de dados **H2**, com o objetivo de realizar o gerenciamento de medicamentos de usuários, incluindo dados sobre bula, frequência e instruções de uso.

## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

## 📂 Estrutura do Projeto

O projeto está organizado nos seguintes pacotes:

- `controller` – Camada de controle da API REST.
- `service` – Lógica de negócios da aplicação.
- `repository` – Interfaces de persistência com Spring Data JPA.
- `entity` – Representações das tabelas do banco de dados.
- `dto` – Objetos de transferência de dados (Data Transfer Object).

## 🧠 Funcionalidades

### Usuários (`/api/users`)
- `POST /register` – Registro de novo usuário.
- `POST /login` – Login com e-mail e senha.

### Medicamentos (`/api/medicamentos`)
- `POST /` – Cadastra um novo medicamento com frequência, bula e instruções.
- `GET /usuario/{userId}` – Lista os medicamentos de um usuário específico.
- `GET /{id}` – Retorna os detalhes de um medicamento por ID.
- `GET /{id}/frequencia` – Retorna a frequência de um medicamento.
- `GET /{id}/bula` – Retorna a bula do medicamento.
- `GET /{id}/instrucoes` – Retorna as instruções de uso do medicamento.

### Bulas (`/api/bulas`)
- `GET /` – Lista todas as bulas cadastradas.
- `GET /{id}` – Retorna os detalhes de uma bula por ID.

## 🧪 DTOs

- `UserDTO`: para autenticação e registro.
- `MedicamentoDTO`: inclui informações sobre o medicamento, bula, frequência e instruções.
- `FrequenciaDTO`: datas e número de vezes ao dia.
- `BulaDTO`: instruções da bula.

## 🛠️ Como Rodar o Projeto

### Pré-requisitos
- JDK 17+
- Maven

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/nome-do-repositorio.git
