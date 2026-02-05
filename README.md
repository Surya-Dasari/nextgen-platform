🚀 NextGen Platform – v1
📌 Overview

NextGen Platform is a full-stack, cloud-native microservices application deployed on OpenShift with a complete CI/CD pipeline using Jenkins.

This project demonstrates real-world DevOps practices including:

Immutable Docker image deployments

Idempotent Kubernetes/OpenShift resources

Secure secret management via Jenkins

End-to-end CI/CD automation

Microservices communication with JWT-based authentication

🧱 Architecture (v1)
Frontend (Node.js / Express)
        |
        |  REST API
        v
Auth Service (Spring Boot) ── JWT
        |
        |  User validation
        v
User Service (Spring Boot) ── PostgreSQL

Platform Components

Frontend: Node.js + Express (UI)

Auth Service: Login, JWT generation

User Service: User creation & validation

Database: PostgreSQL (StatefulSet)

CI/CD: Jenkins

Container Registry: Docker Hub

Orchestration: OpenShift (Sandbox)

🔧 Tech Stack
Layer	Technology
Frontend	Node.js, Express
Backend	Spring Boot (Java 17)
Auth	JWT (HMAC)
Database	PostgreSQL
CI/CD	Jenkins
Containers	Docker
Orchestration	OpenShift
SCM	GitHub
🔐 Configuration & Secrets
🔹 PostgreSQL

ConfigMap: postgres-config

Secret: postgres-secret

POSTGRES_USER

POSTGRES_PASSWORD

POSTGRES_DB

🔹 User Service

ConfigMap: userservice-config

Secret: userservice-db-secret

SPRING_DATASOURCE_URL

SPRING_DATASOURCE_USERNAME

SPRING_DATASOURCE_PASSWORD

🔹 Jenkins Credentials

Secrets are NOT stored in Git.

Credential ID	Purpose
dockerhub-creds	Docker image push
openshift-token	OpenShift authentication
pg-db-user	DB username
pg-db-password	DB password
pg-db-name	DB name

➡️ Secrets are created idempotently during pipeline execution.

🔄 CI/CD Pipeline Flow
Branch Strategy

develop → Active development

main → Stable release (v1)

Pipeline Stages

Checkout source code

Build backend services (Maven)

Build frontend (npm)

Build Docker images

Push images to Docker Hub

Prepare secrets (idempotent)

Deploy to OpenShift

Verify rollout

Key DevOps Practices

Immutable image tags (BUILD_NUMBER)

No latest tag usage

No manual pod restarts

Safe re-runs of pipeline

🚢 Deployment Model

Uses oc apply (declarative)

Rollouts triggered by image change

PostgreSQL runs as StatefulSet

All services exposed via OpenShift Routes

🔍 How Authentication Works
Signup

Frontend → User Service (POST /users)

Password hashed using BCrypt

Stored in PostgreSQL

Login

Frontend → Auth Service (POST /login)

Auth Service calls User Service (/users/validate)

On success → JWT token issued

After Login

JWT stored in browser

Used for authenticated requests

UI shows: “Hey user, welcome”

🧪 CLI Testing (Example)
# Create user
curl -X POST $USER_URL/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"pass1"}'

# Validate user
curl -X POST $USER_URL/users/validate \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"pass1"}'

# Login (JWT)
curl -X POST $AUTH_URL/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"pass1"}'

📦 Versioning Strategy
v1 (Current)

Single OpenShift environment

Jenkins-based CI/CD

Docker Hub registry

Manual branch promotion

Future Versions

v2: Multi-env (dev / stage / prod)

v3: Helm or Kustomize

v4: Monitoring (Prometheus, Grafana)

v5: GitOps (Argo CD)

✅ Why This Is a Strong DevOps Project

This project demonstrates:

Real CI/CD problems & solutions

Secret management best practices

OpenShift-native deployment

Debugging & rollout strategies

Clear separation of responsibilities

✅ Interview-ready
✅ Production-aligned
✅ Not a toy project

👤 Author

Surya Dasari
