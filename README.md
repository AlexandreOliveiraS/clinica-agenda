Clínica Agenda 🩺📆

Fullstack – Spring Boot 3 (Java 21) + React (Vite) + PostgreSQL (Docker)

Sistema de agendamento de consultas para clínica de fisioterapia.
Back-end com autenticação JWT, regras de idempotência para evitar duplicidade de agendamentos, documentação OpenAPI/Swagger e front-end em React com calendário, forms e login.

✨ Funcionalidades

Autenticação JWT via POST /auth/token (usuário simples, sem senha – ideal para POC).

CRUD básico de Pacientes e Fisioterapeutas.

Agendamentos (Consultas) com validação de conflitos de horário e Idempotency-Key para reprocessamentos seguros.

Swagger UI com testes rápidos dos endpoints.

CORS configurado para o front (http://localhost:5173).

Docker Compose para subir o Postgres em 1 comando.

🧱 Arquitetura (visão rápida)
clinica-agenda/
├─ backend/                  # Spring Boot (Java 21)
│  ├─ src/main/java/com/clinica
│  │  ├─ config/             # SecurityConfig, OpenAPI, CORS
│  │  ├─ domain/             # Entidades JPA (Paciente, Fisioterapeuta, Consulta)
│  │  ├─ repo/               # Repositórios Spring Data
│  │  ├─ service/            # Regras (AgendaService, Errors)
│  │  └─ web/                # Controllers REST (AuthController, ApiController + DTOs)
│  └─ src/main/resources/
│     └─ application.yml
│
├─ frontend/                 # React + Vite
│  ├─ src/
│  │  ├─ api/                # axios client
│  │  ├─ context/            # AuthContext (JWT no localStorage)
│  │  ├─ components/         # CalendarView, *Form, DaySchedule…
│  │  └─ pages/              # Login, Dashboard
│  └─ vite.config.js
│
├─ docker-compose.yml        # Banco Postgres
└─ README.md

🛠️ Tecnologias

Back-end

Java 21, Spring Boot 3.x

Spring Web, Spring Security (Resource Server / JWT), Spring Data JPA

Postgres Driver, Spring Validation, Springdoc OpenAPI

Front-end

React 18 + Vite

React Router

Axios

react-big-calendar + date-fns

react-toastify

Infra

PostgreSQL 16 via Docker

⚙️ Pré-requisitos

Docker Desktop

JDK 21 (ou compatível com Spring Boot 3)

Node 18+ (ou 20+)

🚀 Subindo o projeto (dev)
1) Banco (Docker)

Na raiz do projeto:

docker compose up -d


Compose esperado:

services:
  db:
    image: postgres:16
    container_name: clinica-db
    environment:
      POSTGRES_DB: clinica
      POSTGRES_USER: clinica
      POSTGRES_PASSWORD: clinica
    ports:
      - "5434:5432"           # host:container
    volumes:
      - clinica_db:/var/lib/postgresql/data
volumes:
  clinica_db:


Se usar outra porta, ajuste no application.yml.

2) Back-end

Arquivo backend/src/main/resources/application.yml (exemplo):

server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/clinica
    username: clinica
    password: clinica
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
  jackson:
    time-zone: UTC

clinica:
  jwt:
    secret: "mude-este-segredo-super-seguro"


Rodar:

cd backend
mvn spring-boot:run


Swagger:
👉 http://localhost:8081/swagger-ui/index.html

3) Front-end

Crie .env no frontend/:

VITE_API_BASE=http://localhost:8081


Instale e rode:

cd frontend
npm install
npm run dev


App:
👉 http://localhost:5173

🔐 Autenticação

Gerar token
POST /auth/token – body:

{ "username": "alexandre" }


Resposta:

{ "token": "<JWT>" }


Usar no Swagger
Clique em Authorize e cole: Bearer <JWT>.

Front
A tela /login chama /auth/token, salva o JWT no localStorage e redireciona para o dashboard.

🧪 Fluxo de uso (exemplo)

Gerar token (/auth/token).

Criar Paciente (POST /api/pacientes).

Criar Fisioterapeuta (POST /api/fisios).

Agendar Consulta (POST /api/consultas)

Body:

{
  "pacienteId": "UUID_PACIENTE",
  "fisioId":    "UUID_FISIO",
  "inicio":     "2025-09-10T14:00:00",
  "duracaoMin": 30
}


Header obrigatório: Idempotency-Key: <uuid-aleatorio>

Listar agenda do dia (GET /api/consultas?fisioId=...&dia=YYYY-MM-DD)

📚 Endpoints principais
Método	URL	Descrição
POST	/auth/token	Gera JWT simples a partir de username
POST	/api/pacientes	Cria paciente
GET	/api/pacientes/{id}	Busca paciente
POST	/api/fisios	Cria fisioterapeuta
GET	/api/fisios	Lista fisioterapeutas
POST	/api/consultas	Agenda consulta (Idempotency-Key)
GET	/api/consultas?fisioId&dia	Lista consultas do dia por fisio

401 se faltar Authorization: Bearer <token>.
409 se repetir Idempotency-Key (idempotência) ou houver conflito de horário.

🧠 Idempotência (por que e como)

A criação de consultas exige header Idempotency-Key.

Caso o mesmo payload seja reenviado com a mesma chave (ex: usuário clicou 2x), o back-end retorna 409 – “Consulta já criada (idempotência)” e não cria duplicata.

Gere um UUID aleatório no front a cada tentativa de agendamento.

🔐 CORS

No SecurityConfig, o CORS permite o front dev:

cfg.setAllowedOrigins(List.of("http://localhost:5173"));
cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key"));
cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));

🧩 Dicas / Solução de problemas

Porta 8081 em uso: feche outro Spring/serviço ou altere server.port.

JWTDecoder bean error: configure clinica.jwt.secret no application.yml.

401 no Swagger: clique em Authorize e informe Bearer <token>.

409 no POST /api/consultas: idempotência (mesma chave) ou conflito de horário.

CRLF/LF warnings no Git (Windows): normal; não afeta a execução.

🧪 Scripts úteis

Back-end

cd backend
mvn clean package
mvn spring-boot:run


Front-end

cd frontend
npm install
npm run dev
npm run build


Banco

docker compose up -d
docker compose logs -f
docker compose down -v

📦 Deploy (visão geral)

Back-end: gerar jar com mvn clean package e subir em um serviço Java (Docker/K8s/EC2/Heroku/Render).

Front-end: npm run build e servir os arquivos estáticos (Nginx, Vercel, Netlify).

Banco: instância gerenciada de Postgres (RDS, Cloud SQL, etc.).

Ajuste CORS e URLs (ex.: VITE_API_BASE) para o ambiente de produção.

📄 Licença

Projeto acadêmico/POC – use livremente.

🙌 Agradecimentos

Spring / Spring Security / Spring Data

React / Vite / react-big-calendar

Comunidade Open Source

📫 Contato

Autor: Alexandre Oliveira

Repositório: https://github.com/AlexandreOliveiraS/clinica-agenda
