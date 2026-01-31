# NextGen Platform – System Overview

## Components
- **nextgen-ui**: Node.js UI serving static HTML/CSS/JS
- **apiservice**: Entry point for UI → backend
- **authservice**: Authentication + JWT issuance
- **userservice**: User persistence & validation
- **postgres**: Central database

## High-level Flow
UI → API Service → Auth Service → User Service → PostgreSQL

## Deployment Platform
- Red Hat OpenShift (Sandbox)
- Docker images via Docker Hub
- ClusterIP services + OpenShift Routes
