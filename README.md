# MobileZBD

MobileZBD is a full-stack mini marketplace for phones and accessories.

It includes:
- Spring Boot REST API with JWT authentication and role-based authorization
- React + Vite frontend with protected routes
- PostgreSQL persistence with Spring Data JPA
- Docker and Docker Compose for local full-stack run
- GitHub Actions CI with staged tests and Render deployment on main

## Project Status (Current)

Implemented modules:
- Authentication: register/login with JWT
- Product catalog: list/search/filter/detail
- Cart and stock validation
- Checkout and order creation
- Customer profile and customer orders
- Seller product management and seller profile/orders
- Admin product and order management
- Frontend pages for customer, seller, and admin flows
- Backend tests (service + integration)
- Frontend tests (Vitest + Testing Library)
- CI pipeline with sequential test stages
- Auto-deploy trigger to Render on push to main

## Tech Stack

Backend:
- Java 17
- Spring Boot 3.3.5
- Spring Web, Spring Security, Spring Data JPA, Validation
- PostgreSQL
- JWT (jjwt)

Frontend:
- React 19 + Vite
- React Router
- Axios
- Vitest + Testing Library
- jsPDF

DevOps:
- Docker + Docker Compose
- GitHub Actions
- Render Deploy Hook

## Repository Structure

- Backend source: src/main/java
- Backend config: src/main/resources
- Backend tests: src/test/java
- Frontend app: frontend/src
- Frontend container config: frontend/Dockerfile, frontend/nginx.conf
- CI workflow: .github/workflows/ci-cd.yml

## API Summary

Public:
- POST /api/auth/register
- POST /api/auth/login
- GET /api/home
- GET /api/cart
- POST /api/cart/validate
- GET /api/products
- GET /api/products/search
- GET /api/products/{id}
- GET /api/product/{id}

Customer/Seller:
- PATCH /api/products/stock
- POST /api/orders

Customer:
- GET /api/customer/profile
- GET /api/customer/orders

Seller:
- POST /api/seller/products
- GET /api/seller/products
- PUT /api/seller/products/{id}
- DELETE /api/seller/products/{id}
- GET /api/seller/profile
- GET /api/seller/orders

Admin:
- POST /api/admin/products
- PUT /api/admin/products/{id}
- DELETE /api/admin/products/{id}
- GET /api/admin/products
- GET /api/admin/orders
- PUT /api/admin/orders/{id}
- DELETE /api/admin/orders/{id}

## Security Model

- JWT-based stateless authentication
- BCrypt password hashing
- Role-based access:
   - ROLE_ADMIN for /api/admin/**
   - ROLE_CUSTOMER for /api/customer/**
   - ROLE_SELLER for /api/seller/**
- Method-level guards are used on protected operations

## Local Development

### Prerequisites
- Java 17
- Maven Wrapper (included)
- Node.js 20+
- npm
- Docker Desktop (recommended for DB/full stack)

### Option A: Backend + Docker DB, Frontend in dev mode

1. Start PostgreSQL container:

```bash
docker compose up -d db
```

2. Start backend:

Windows:

```bash
mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

3. Start frontend:

```bash
cd frontend
npm install
npm run dev
```

Notes:
- Frontend dev server proxies /api to http://localhost:9090
- Backend default local port is 9090

### Option B: Full stack with Docker Compose

```bash
docker compose up --build
```

Services:
- Frontend (Nginx): http://localhost:9090
- Backend API (via Nginx proxy): http://localhost:9090/api
- PostgreSQL: localhost:5433

## Environment Variables

Backend reads these values:
- DB_URL
- DB_USER
- DB_PASSWORD
- JWT_SECRET
- JWT_EXPIRATION_MS
- ADMIN_EMAIL
- ADMIN_PASSWORD
- PORT

Compose provides defaults for local development, but set secure values for production.

## Test Commands

Backend all tests:

```bash
mvnw.cmd test
```

Backend staged groups (same grouping used in CI):

```bash
mvnw.cmd -DfailIfNoTests=false -Dtest='com.mobilezbd.service.*Test' test
mvnw.cmd -DfailIfNoTests=false -Dtest='com.mobilezbd.repository.*Test' test
mvnw.cmd -DfailIfNoTests=false -Dtest='com.mobilezbd.integration.*Test' test
```

Frontend tests:

```bash
cd frontend
npm run test
```

## CI/CD

Workflow: .github/workflows/ci-cd.yml

Triggers:
- Pull requests to develop/main (opened, synchronize, reopened, ready_for_review)
- Push to main

PR pipeline stages (sequential):
1. Service tests
2. Repository tests
3. Integration tests

Deployment:
- On push to main, workflow triggers Render Deploy Hook

Required GitHub secret:
- RENDER_DEPLOY_HOOK

## Branching and Release Flow

- develop is default integration branch
- main is release branch
- Feature branches are created from develop

Flow:
1. feature/* -> PR -> develop
2. Required checks + reviewer approval
3. develop -> PR -> main for release
4. Merge to main triggers Render deployment

Recommended protection rules for develop and main:
- No direct pushes
- Require pull request
- Require status checks to pass
- Require at least 1 approving review

## Default Seed/Admin Credentials (Local)

- Email: admin@mobilezbd.com
- Password: admin123

Use only for local/dev. Change in real environments.
