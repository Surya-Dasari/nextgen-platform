# NextGen Platform – Architecture Overview

## 1. What is NextGen?

NextGen is a simple but realistic web application designed to understand:
- Microservices communication
- Authentication flows
- Database evolution
- OpenShift deployment
- CI/CD concepts

The goal is learning, not complexity.

---

## 2. High-Level Architecture

Browser (UI)
  → API Service (Gateway)
    → Auth Service (Authentication)
      → User Service (User + DB)
        → PostgreSQL

---

## 3. Services Overview

### Frontend (NextGen UI)
- Static HTML + JavaScript
- No React / Angular
- Talks only to API Service
- Stores JWT in browser

### API Service
- Gateway for frontend
- Routes requests
- No business logic

### Auth Service
- Validates login
- Generates JWT

### User Service
- Manages users
- Talks to DB

### Database
- PostgreSQL
- Persistent storage

---

## 4. Key Learning Outcomes

- Service-to-service communication
- Stateless authentication
- Platform behavior (OpenShift)
