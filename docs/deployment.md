# CloudPilot — Cloud Deployment Guide

This guide covers 100% free cloud deployment of CloudPilot across top platforms including **Render**, **Vercel**, **Netlify**, **Railway**, and **Neon PostgreSQL**.

---

## Architecture Overview

```
                      +-----------------------------+
                      |       Vercel / Netlify       |
                      |   or Render Static Site     |
                      |  (React 18 + Vite Frontend) |
                      +--------------+--------------+
                                     |
                             HTTPS / REST API
                                     v
                      +-----------------------------+
                      |        Render Web App       |
                      |      or Railway Service     |
                      | (Spring Boot 3.3 / Java 21) |
                      +-------+-------------+-------+
                              |             |
           REST / JSON Embeds |             | JDBC + SSL (Pooled)
                              v             v
       +----------------------------+  +-------------------------------+
       |       Render Web App       |  |       Neon Serverless DB       |
       |      or Railway Service    |  |     (Cloud PostgreSQL 16)     |
       | (FastAPI + NLP Embeddings) |  +-------------------------------+
       +----------------------------+
```

---

## Prerequisites & Pre-Configured Cloud Database

CloudPilot is pre-configured to connect directly to a live serverless cloud database on Neon:

| Config Key | Value / Environment Variable |
|---|---|
| **Database Engine** | PostgreSQL 16 (Serverless with SSL Pooling) |
| **`CLOUD_DB_URL`** | `jdbc:postgresql://ep-twilight-tooth-ax4dzdsg-pooler.c-4.us-east-2.aws.neon.tech/neondb?sslmode=require` |
| **`CLOUD_DB_USER`** | `neondb_owner` |
| **`CLOUD_DB_PASSWORD`**| `npg_bvsK7ClNAhn2` |
| **`JWT_SECRET`** | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |

---

## Option 1: 1-Click Multi-Service Deployment via Render (Recommended)

Render provides free Docker web services and free static sites. CloudPilot includes a root `render.yaml` blueprint.

### Steps:
1. Fork or push this repository to your GitHub account: `https://github.com/sangam-builds/cloudpilot`
2. Go to [Render Dashboard](https://dashboard.render.com/) and click **New +** -> **Blueprint**.
3. Connect your repository.
4. Render will automatically detect `render.yaml` and instantiate 3 free services:
   - **`cloudpilot-ai-service`**: FastAPI Python / Docker container
   - **`cloudpilot-backend`**: Spring Boot Java 21 Docker container with Neon DB & Flyway
   - **`cloudpilot-frontend`**: Vite React SPA static site
5. Click **Apply**.
6. Once deployed, open the frontend URL (e.g., `https://cloudpilot-frontend.onrender.com`).

---

## Option 2: Hybrid Deployment (Vercel Frontend + Render Backend)

### Step 2A: Deploy Backend & AI on Render
1. **AI Service**:
   - New **Web Service** -> Build from Dockerfile (`ai-service/Dockerfile`).
   - Note the deployed URL (e.g. `https://cloudpilot-ai.onrender.com`).
2. **Backend**:
   - New **Web Service** -> Build from Dockerfile (`backend/Dockerfile`).
   - Add Environment Variables:
     - `SPRING_PROFILES_ACTIVE`: `docker`
     - `CLOUD_DB_URL`: `jdbc:postgresql://ep-twilight-tooth-ax4dzdsg-pooler.c-4.us-east-2.aws.neon.tech/neondb?sslmode=require`
     - `CLOUD_DB_USER`: `neondb_owner`
     - `CLOUD_DB_PASSWORD`: `npg_bvsK7ClNAhn2`
     - `CLOUDPILOT_AI_SERVICE_BASE_URL`: `https://cloudpilot-ai.onrender.com`
     - `JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`
   - Note the deployed URL (e.g. `https://cloudpilot-backend.onrender.com`).

### Step 2B: Deploy Frontend on Vercel
1. Go to [Vercel Dashboard](https://vercel.com/new) -> Import `cloudpilot`.
2. Set **Root Directory** to `frontend`.
3. Framework Preset: **Vite**.
4. Add Environment Variable:
   - `VITE_API_BASE_URL`: `https://cloudpilot-backend.onrender.com/api`
5. Click **Deploy**. Vercel will build and serve the single page application with automatic HTTPS and edge routing via `frontend/vercel.json`.

---

## Option 3: Deploy Frontend on Netlify

1. Go to [Netlify](https://app.netlify.com/) -> **Add new site** -> **Import an existing project**.
2. Select your GitHub repository.
3. Base directory: `frontend`
4. Build command: `npm run build`
5. Publish directory: `frontend/dist`
6. Environment Variables:
   - `VITE_API_BASE_URL`: `https://cloudpilot-backend.onrender.com/api`
7. Click **Deploy Site**. Netlify automatically uses `frontend/netlify.toml` for 200 rewrites on client routes.

---

## Option 4: Full Stack Docker on Any Free VPS (e.g. Oracle Cloud Free Tier)

1. Clone the repository on your VM:
   ```bash
   git clone https://github.com/sangam-builds/cloudpilot.git
   cd cloudpilot
   ```
2. Run with production compose:
   ```bash
   docker compose up -d --build
   ```
3. Access:
   - **Frontend UI**: `http://<YOUR_SERVER_IP>:3000`
   - **Spring Boot Backend**: `http://<YOUR_SERVER_IP>:8088/swagger-ui.html`
   - **AI Microservice**: `http://<YOUR_SERVER_IP>:8000/docs`
   - **Grafana Monitoring**: `http://<YOUR_SERVER_IP>:3001` (admin / admin)
   - **Prometheus**: `http://<YOUR_SERVER_IP>:9090`

---

## Free-Tier Operational Tips & Cold Starts

> [!NOTE]
> **Free Tier Cold Starts**: Render and Railway free-tier instances sleep after 15 minutes of inactivity. When woke by the first HTTP request, cold starts may take ~30–45 seconds for Java/Spring Boot to initialize.
> Subsequent requests will be near-instantaneous (<50ms).

> [!TIP]
> **Serverless Neon DB Connection Pool**: The HikariCP configuration uses `keepalive-time: 15000` and `connection-test-query: SELECT 1` to gracefully handle Neon's serverless autosuspension without dropped queries.
