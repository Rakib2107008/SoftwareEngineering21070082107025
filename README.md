# MobileZBD - Phone and Accessories Mini Marketplace

MobileZBD is a lab project mini marketplace built with React frontend, Spring Boot REST API backend, PostgreSQL, JWT security, Docker, and GitHub Actions CI/CD with Render deployment.

## Tech Stack
- Frontend: React + React Router + Axios + jsPDF
- Backend: Spring Boot, Spring Security, Spring Data JPA, JWT
- Database: PostgreSQL
- DevOps: Docker, Docker Compose, GitHub Actions, Render

## Architecture Diagram
- Add your architecture image at: docs/architecture-diagram.png

## ER Diagram
- Add your ER image at: docs/er-diagram.png

## API Endpoints

| Method | URL | Auth | Description |
|---|---|---|---|
| POST | /api/auth/register | Public | Register customer |
| POST | /api/auth/login | Public | Login and receive JWT |
| GET | /api/home | Public | Home page data (recent + trending) |
| GET | /api/cart | Public | Product cards by category |
| POST | /api/cart/validate | Public | Validate stock for cart items |
| GET | /api/products | Public | Public product list |
| GET | /api/products/{id} | Public | Product detail by id |
| GET | /api/product/{id} | Public | Product detail alias |
| PATCH | /api/products/stock | CUSTOMER | Deduct stock for checkout |
| POST | /api/orders | CUSTOMER | Create order sell history |
| GET | /api/customer/profile | CUSTOMER | Logged-in customer profile |
| GET | /api/customer/orders | CUSTOMER | Logged-in customer orders |
| POST | /api/admin/products | ADMIN | Add product |
| PUT | /api/admin/products/{id} | ADMIN | Update product |
| DELETE | /api/admin/products/{id} | ADMIN | Delete product |
| GET | /api/admin/products | ADMIN | View all products |
| GET | /api/admin/orders | ADMIN | View all orders |
| PUT | /api/admin/orders/{id} | ADMIN | Update order |
| DELETE | /api/admin/orders/{id} | ADMIN | Delete order |

## Local Run

### Backend
1. Copy `.env.example` values into your local environment.
2. Run PostgreSQL and set DB variables.
3. Run:
```bash
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Docker Run
```bash
docker compose up --build
```

Containerized runtime:
- Frontend (Nginx + built React): http://localhost:9090
- Backend API (proxied through frontend): http://localhost:9090/api/*
- PostgreSQL: localhost:5433

Notes:
- The backend may take up to ~50 seconds on first startup while Spring initializes.
- If the page opens before backend is ready, frontend API calls now auto-retry until backend is available.

## CI/CD (GitHub Actions)
Workflow file: `.github/workflows/ci-cd.yml`

- test job: starts postgres service and runs `mvn test`
- build job: packages app, builds Docker image, pushes to Docker Hub
- deploy job: triggers Render deploy hook via `curl` using `RENDER_DEPLOY_HOOK`

Required GitHub secrets:
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `RENDER_DEPLOY_HOOK`

## Render Deployment
1. Create a Render Web Service from Docker image.
2. Configure env vars in Render dashboard:
   - DB_URL
   - DB_USER
   - DB_PASSWORD
   - JWT_SECRET
   - ADMIN_EMAIL
   - ADMIN_PASSWORD
3. Add managed PostgreSQL or external DB and update DB_URL.
4. Use Deploy Hook with GitHub Actions.

Live URL: add here
GitHub Repo: add here

## Branch Strategy
- main: protected, no direct push
- develop: integration branch
- feature/backend-* and feature/frontend-* for scoped work

Merge flow:
1. feature -> develop
2. develop -> main

## Team Members and Contributions
- Member 1 (Backend + DevOps): backend APIs, auth/security, tests, Docker, CI/CD
- Member 2 (Frontend + DB docs): frontend pages/components/context, API integration, README/docs diagrams
