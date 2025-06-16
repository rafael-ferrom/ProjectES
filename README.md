# Tech Pharmacy: Sistema de Gestão de Medicamentos 🩺

Tech Pharmacy é uma aplicação Full Stack projetada para empoderar pacientes e cuidadores na gestão de tratamentos médicos. A plataforma oferece uma interface simples e inteligente para controlar medicamentos, horários, estoque e informações relevantes, garantindo segurança e aumentando a adesão ao tratamento.

O projeto é composto por uma API RESTful robusta desenvolvida com Spring Boot e um frontend reativo construído com Vue.js. Todo o ambiente é containerizado com Docker, garantindo uma configuração e execução simples e consistente.

## ⚕️ Gestão de Tratamentos: Cadastro de medicamentos com definição de dosagem, frequência e duração.

- 📦 Controle de Estoque e Validade: Gerenciamento de caixas de remédios, com contagem de comprimidos e alertas sobre a data de validade.
- 🔔 Sistema de Notificações: Lembretes proativos para doses próximas e alertas para doses esquecidas, com um painel de histórico.
- ℹ️ Consulta de Bulas: Um catálogo de medicamentos com informações detalhadas, incluindo princípio ativo, fabricante e foto.
- 🛒 Localização de Farmácias: Ferramenta integrada com a API do Google Maps para encontrar farmácias próximas.
- 👤 Autenticação de Usuários: Sistema de registro e login para gerenciamento de perfis individuais.

## 🚀 Tecnologias Utilizadas

- Backend	Java 17, Spring Boot, Spring Data JPA, Spring Security, Maven
- Frontend	Vue.js (Vue 2), Vuetify, Pinia (State Management), Axios
- Banco de Dados	Microsoft SQL Server
- Infraestrutura	Docker, Docker Compose, Nginx (Reverse Proxy)

## 🏛️ Arquitetura do Sistema

A aplicação segue uma arquitetura moderna com serviços desacoplados, facilitando a manutenção e escalabilidade.

- Nginx: Atua como proxy reverso, direcionando o tráfego do usuário. Requisições para a API (/api/*) são encaminhadas para o Backend, enquanto as demais são atendidas pelo Frontend.
- Frontend (Vue.js): Interface do usuário interativa que consome os dados da API Backend.
- Backend (Spring Boot): Expõe a API RESTful, contém toda a lógica de negócio e se comunica com o banco de dados.
- Banco de Dados (SQL Server): Armazena todos os dados da aplicação de forma persistente.

## 🚀 Como Executar o Projeto (Com Docker)
A maneira mais simples e recomendada de executar o projeto é utilizando Docker e Docker Compose, que gerenciam todo o ambiente para você.

Pré-requisitos
- Docker
- Docker Compose (geralmente já vem com o Docker Desktop)
Passos:

### Clone este repositório para a sua máquina local:

```Bash

git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```
A partir da raiz do projeto, execute o seguinte comando para construir as imagens e iniciar os contêineres:

```Bash
docker compose up --build
```
Após a conclusão do build, a aplicação estará disponível nos seguintes endereços:

Aplicação Frontend: http://localhost
API Backend: http://localhost/api (acessível através do proxy Nginx)

## 📖 Endpoints da API

Usuários (/api/users)
- POST /register: Registro de novo usuário.
- POST /login: Login com e-mail e senha.
- Tratamentos (/api/medicamentos)
- `POST /`: Cadastra um novo plano de tratamento para um usuário.
- `GET /usuario/{userId}`: Lista os tratamentos de um usuário específico.
- `POST /{id}/doses`: Registra que uma dose de um tratamento foi tomada.
- `Estoque (/api/estoque)`
- `POST /comprar`: Adiciona uma ou mais caixas de um medicamento ao estoque do usuário.
- `GET /usuario/{userId}`: Lista todos os itens de estoque de um usuário.
- `Notificações (/api/notificacoes)`
- `POST /`: Salva uma nova notificação gerada pelo frontend.
- `GET /usuario/{userId}`: Retorna o histórico de notificações de um usuário.
- `POST /marcar-como-lidas`: Marca uma lista de notificações como lidas.
- `Bulas (/api/bulas)`
- `GET /`: Lista todas as bulas (medicamentos base) cadastradas no sistema.
- `GET /{id}`: Retorna os detalhes de uma bula por ID.

## 📂 Estrutura do Backend

- config: Classes de configuração (Ex: Spring Security).
- controller: Camada de controle da API REST, responsável por receber as requisições HTTP.
- dto: Objetos de Transferência de Dados (Data Transfer Objects) para a comunicação entre camadas.
- entity: Classes que representam as tabelas do banco de dados (JPA Entities).
- repository: Interfaces de persistência de dados com Spring Data JPA.
- service: Camada onde reside a lógica de negócios da aplicação.