# Task Management API - Backend Intern Assignment

🚀 **Live Demo:** https://assignment.nishantdd.dev

⚡ No setup required — fully deployed (Dockerized + CI/CD)

🔐 Quick Test Credentials

Admin:
- Email: admin@example.com
- Password: 12345678
  → Can view and manage all tasks

User:
- Email: user@example.com
- Password: 12345678
  → Can only manage own tasks

👉 Try:
- Creating tasks as user
- Logging in as admin and viewing all tasks

**Docker Hub:** https://hub.docker.com/r/nishantdd/assignment-api

A secure, scalable REST API built with **Java 21 + Spring Boot 4**, featuring JWT authentication, role-based access control, and a vanilla JS frontend — all served from a single deployable service.

---

## Quick Start

### Option 1 — Docker (Recommended)

The image is published to Docker Hub on every push to `main`. No build step needed.

```bash
git clone https://github.com/nishant-dd-24/assignment.git
cd assignment

cp .env.example .env

docker compose up --pull always
```

| Service                 | Local URL                                   | Live URL                                          |
|-------------------------|---------------------------------------------|---------------------------------------------------|
| Frontend (Landing page) | http://localhost:8080                       | https://assignment.nishantdd.dev                  |
| Login                   | http://localhost:8080/login.html            | https://assignment.nishantdd.dev/login.html       |
| Register                | http://localhost:8080/register.html         | https://assignment.nishantdd.dev/register.html    |
| Dashboard               | http://localhost:8080/dashboard.html        | https://assignment.nishantdd.dev/dashboard.html   |
| Swagger UI              | http://localhost:8080/swagger-ui/index.html | https://assignment.nishantdd.dev/swagger-ui/index.html |
| API Docs (JSON)         | http://localhost:8080/api-docs              | https://assignment.nishantdd.dev/api-docs         |

### Option 2 — Local (requires PostgreSQL running)

```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE assignment_db;"
psql -U postgres -c "CREATE USER assignment_user WITH PASSWORD 'yourpassword';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE assignment_db TO assignment_user;"

# 2. Create your .env file (or export each variable individually)
cp .env.example .env
# Edit .env with your local values

# 3. Build and run (Spring Boot picks up .env automatically via default fallbacks)
./mvnw spring-boot:run
```

---

## Tech Stack

| Layer            | Technology                           |
|------------------|--------------------------------------|
| Language         | Java 21                              |
| Framework        | Spring Boot 4.0.5                    |
| Security         | Spring Security + JJWT 0.12.7        |
| Database         | PostgreSQL 16                        |
| ORM              | Spring Data JPA (Hibernate)          |
| Validation       | Jakarta Bean Validation              |
| API Docs         | SpringDoc OpenAPI 3.0.3 (Swagger UI) |
| Frontend         | Vanilla HTML / CSS / JavaScript      |
| Build            | Maven                                |
| Containerisation | Docker + Docker Compose              |
| Testing          | JUnit 5 + Mockito                    |
| CI/CD            | GitHub Actions                       |
| Image Registry   | Docker Hub                           |

---

## Features

### Authentication & Security
- User registration with **BCrypt password hashing**
- JWT-based login — stateless, no sessions
- Protected routes via `Authorization: Bearer <token>` header
- Custom `401` / `403` JSON error responses (no Spring default HTML pages)
- Default admin account seeded on startup via `AdminInitializer`

### Role-Based Access Control
| Action                           | USER | ADMIN |
|----------------------------------|------|-------|
| Register / Login                 | Yes  | Yes   |
| View own tasks                   | Yes  | Yes   |
| Create / edit / delete own tasks | Yes  | Yes   |
| View **all** tasks               | No   | Yes   |
| Edit / delete **any** task       | No   | Yes   |
| Promote USER to ADMIN            | No   | Yes   |

### Task Management (CRUD)
- Create tasks with title, description, and status
- Status lifecycle: `TODO` -> `IN_PROGRESS` -> `DONE`
- Ownership enforced — users only see and modify their own tasks
- Admin has full visibility and control across all tasks

### Developer Experience
- Standardised error responses with `ErrorCode` enum, timestamp, and request path
- Enum validation with human-readable messages (shows allowed values on bad input)
- API versioning via `/api/v1/` prefix
- Swagger UI with Bearer auth support pre-configured
- Friendly redirect routes: `/login`, `/register`, `/docs`

---

## API Endpoints

### Auth — `/api/v1/auth`

| Method  | Endpoint                          | Auth        | Description              |
|---------|-----------------------------------|-------------|--------------------------|
| `POST`  | `/api/v1/auth/register`           | No          | Register a new user      |
| `POST`  | `/api/v1/auth/login`              | No          | Login and receive JWT    |
| `GET`   | `/api/v1/auth/me`                 | JWT         | Get current user profile |
| `PATCH` | `/api/v1/auth/users/{id}/promote` | JWT + ADMIN | Promote USER to ADMIN    |

### Tasks — `/api/v1/tasks`

| Method   | Endpoint             | Auth | Description                         |
|----------|----------------------|------|-------------------------------------|
| `GET`    | `/api/v1/tasks`      | JWT  | Get tasks (admin = all, user = own) |
| `GET`    | `/api/v1/tasks/{id}` | JWT  | Get task by ID                      |
| `POST`   | `/api/v1/tasks`      | JWT  | Create a new task                   |
| `PUT`    | `/api/v1/tasks/{id}` | JWT  | Update a task                       |
| `DELETE` | `/api/v1/tasks/{id}` | JWT  | Delete a task                       |

### Request / Response Examples

**Register**
```json
POST /api/v1/auth/register
{
  "name": "Nishant Kumar",
  "email": "nishant@example.com",
  "password": "secret123"
}

// 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "name": "Nishant Kumar",
  "email": "nishant@example.com",
  "role": "USER"
}
```

**Create Task**
```json
POST /api/v1/tasks
Authorization: Bearer <token>
{
  "title": "Prepare API documentation",
  "description": "Add OpenAPI annotations to all endpoints",
  "status": "TODO"
}

// 201 Created
{
  "id": 42,
  "title": "Prepare API documentation",
  "description": "Add OpenAPI annotations to all endpoints",
  "status": "TODO",
  "ownerName": "Nishant Kumar",
  "createdAt": "2026-04-23T10:15:30"
}
```

**Error Response (standardised)**
```json
// 404 Not Found
{
  "path": "/api/v1/tasks/99",
  "method": "GET",
  "status": 404,
  "message": "Task not found",
  "errorCode": "NOT_FOUND",
  "timestamp": "2026-04-23T10:15:30",
  "errors": {}
}
```

---

## Database Schema

```
users
├── id            BIGSERIAL PRIMARY KEY
├── name          VARCHAR NOT NULL
├── email         VARCHAR NOT NULL UNIQUE
├── password      VARCHAR NOT NULL          -- BCrypt hashed
├── role          VARCHAR NOT NULL          -- USER | ADMIN
├── created_at    TIMESTAMP
└── updated_at    TIMESTAMP

tasks
├── id            BIGSERIAL PRIMARY KEY
├── title         VARCHAR(100) NOT NULL
├── description   VARCHAR(500)
├── status        VARCHAR NOT NULL          -- TODO | IN_PROGRESS | DONE
├── owner_id      BIGINT NOT NULL -> users(id)
├── created_at    TIMESTAMP
└── updated_at    TIMESTAMP
```

Schema is managed automatically via `spring.jpa.hibernate.ddl-auto=update`.

---

## Project Structure

```
src/main/java/com/nishant/assignment/
├── config/
│   ├── AdminInitializer.java     # Seeds default admin on startup
│   ├── SecurityConfig.java       # JWT filter chain, CORS, session policy
│   └── SwaggerConfig.java        # OpenAPI + Bearer auth scheme
├── controller/
│   ├── AuthController.java       # Register, login, me, promote
│   ├── TaskController.java       # CRUD endpoints
│   └── RedirectController.java   # Friendly URL redirects
├── dto/                          # Request/response records with Swagger annotations
├── entity/                       # JPA entities (User, Task) + enums (Role, TaskStatus)
├── exception/
│   ├── ErrorCode.java            # Application error code enum
│   ├── ExceptionUtil.java        # Factory for typed exceptions
│   ├── GlobalExceptionHandler.java
│   ├── custom/                   # Typed exception classes
│   ├── response/                 # ErrorResponse, ErrorResponseFactory, Writer
│   └── security/                 # CustomAccessDeniedHandler, AuthenticationEntryPoint
├── repository/                   # Spring Data JPA interfaces
├── security/
│   ├── JwtUtil.java              # Token generation, extraction, validation
│   ├── JwtProperties.java        # Bound JWT config from application.properties
│   ├── JwtAuthFilter.java        # OncePerRequestFilter — validates tokens
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java
    └── TaskService.java

src/main/resources/
├── static/
│   ├── index.html                # Landing page
│   ├── login.html
│   ├── register.html
│   └── dashboard.html            # Protected task manager UI
└── application.properties
```

---

## Security Implementation

- **Password hashing** — BCrypt with default strength (10 rounds)
- **JWT** — HS256 signed with a 256-bit secret; contains `sub` (email), `role`, `jti` (unique ID), `iat`, `exp`
- **Stateless sessions** — `SessionCreationPolicy.STATELESS`; no cookies or server-side state
- **Filter chain** — `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter`; expired/invalid tokens are caught and return structured `401` JSON immediately
- **Method security** — `@EnableMethodSecurity` + `@PreAuthorize("hasRole('ADMIN')")` on admin-only endpoints
- **Input sanitisation** — `@Valid` + Bean Validation annotations on all request DTOs; validation errors return field-level error maps
- **Non-root container** — Dockerfile creates a dedicated `app` system user; the process never runs as root inside Docker

---

## Running Tests

```bash
./mvnw test
```

Tests cover `AuthService` and `TaskService` using JUnit 5 + Mockito:

| Test                                           | Covers                   |
|------------------------------------------------|--------------------------|
| `register_shouldCreateUserAndReturnToken`      | Happy path registration  |
| `register_shouldThrowIfEmailExists`            | Duplicate email guard    |
| `login_shouldThrowIfPasswordInvalid`           | Wrong password -> 401    |
| `login_shouldThrowIfUserNotFound`              | Unknown email -> 401     |
| `promoteUser_shouldUpgradeRoleToAdmin`         | Admin promotion          |
| `promoteUser_shouldFailIfAlreadyAdmin`         | Re-promotion guard       |
| `getAllTasks_adminShouldReturnAllTasks`        | Admin sees all tasks     |
| `getAllTasks_userShouldReturnOwnTasks`         | User sees only own tasks |
| `getTask_shouldDenyAccessForDifferentUser`     | Ownership enforcement    |
| `createTask_shouldAssignOwnerAndDefaultStatus` | Default TODO status      |
| `updateTask_shouldDenyIfNotOwnerOrAdmin`       | Update access control    |
| `deleteTask_adminCanDeleteAnyTask`             | Admin delete privilege   |

---

## Environment Variables

All variables are loaded from a `.env` file in the project root. Copy `.env.example` to get started:

```bash
cp .env.example .env
```

| Variable                     | Default                | Description                              |
|------------------------------|------------------------|------------------------------------------|
| `SPRING_DATASOURCE_USERNAME` | `assignment_user`      | Database username                        |
| `SPRING_DATASOURCE_PASSWORD` | *(required)*           | Database password                        |
| `JWT_SECRET`                 | *(256-bit Base64 key)* | HS256 signing secret                     |
| `JWT_EXPIRATION`             | `3600`                 | Token TTL **in seconds** (3600 = 1 hour) |
| `ADMIN_EMAIL`                | `admin@example.com`    | Seeded admin email                       |
| `ADMIN_PASSWORD`             | `12345678`             | Seeded admin password                    |
| `ADMIN_NAME`                 | `Admin`                | Seeded admin display name                |
| `JAVA_OPTS`                  | `-Xms256m -Xmx512m`    | JVM memory flags passed to the container |

---

## CI/CD and Deployment

The project is deployed to a VPS via a GitHub Actions pipeline that triggers on every push to `main`.

### Pipeline steps (`deploy.yml`)

1. Check out the repository
2. Log in to Docker Hub using repository secrets
3. Build the Docker image and tag it as `nishantdd/assignment-api:latest`
4. Push the image to Docker Hub
5. SSH into the VPS and run `docker compose -f docker-compose.prod.yml up -d --force-recreate`
6. Prune dangling images to keep the server clean

### Docker Compose file layout

The project uses three compose files to keep local and production concerns separate:

| File                        | Purpose                                                               |
|-----------------------------|-----------------------------------------------------------------------|
| `docker-compose.yml`        | Base configuration — image, environment variables, healthcheck, volumes |
| `docker-compose.override.yml` | Local development — exposes port `8080` to the host               |
| `docker-compose.prod.yml`   | Production — uses `expose` instead of `ports` so only the reverse proxy can reach the container |

Locally, Docker Compose automatically merges `docker-compose.yml` and `docker-compose.override.yml`, so `docker compose up` works without any extra flags.

On the VPS, the pipeline explicitly selects the production overlay:

```bash
docker compose -f docker-compose.prod.yml up -d --force-recreate
```

### Deployment Architecture (Brief)

- Nginx reverse proxy handles HTTPS and routes traffic to the API container
- Services communicate via Docker internal network (no exposed backend ports)
- Stateless API allows horizontal scaling behind the proxy

### Required GitHub Actions secrets

| Secret               | Description                              |
|----------------------|------------------------------------------|
| `DOCKER_USERNAME`    | Docker Hub username                      |
| `DOCKER_PASSWORD`    | Docker Hub access token                  |
| `SERVER_IP`          | VPS IP address or hostname               |
| `SSH_PRIVATE_KEY`    | Private key for SSH access to the VPS    |

---

## Scalability Notes

**Stateless Authentication**
JWT tokens carry all identity and role information. No session store is needed, so every API instance is fully independent. Adding more instances behind a load balancer requires zero coordination.

**Horizontal Scaling**
Because there is no shared in-process state, the application can scale to N identical containers behind an AWS ALB, GCP Load Balancer, or Nginx upstream with no configuration changes.

**API Versioning**
All routes are prefixed `/api/v1/`. Breaking changes ship in `/api/v2/` with no disruption to existing clients or integrations.

**Modular Layered Architecture**
The `controller -> service -> repository` separation means new business domains (e.g., comments, file attachments, notifications) plug in as independent packages without touching existing code.

**Caching (future)**
Frequently read endpoints such as `GET /api/v1/tasks` can be accelerated with Redis via Spring Cache — a single `@Cacheable` annotation on the service method, with a Redis Docker service added to `docker-compose.yml`.

**Observability (future)**
Spring Actuator + Micrometer can expose metrics to Prometheus/Grafana with one dependency addition, enabling latency, error rate, and JVM monitoring in production.

**Containerisation**
Docker Compose bundles the application and PostgreSQL together. The same images deploy unchanged to AWS ECS, GCP Cloud Run, Railway, or Render.

---

## License

This project was built as a backend internship selection assignment for Primetrade.ai.