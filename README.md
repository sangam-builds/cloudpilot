# 🚀 CloudPilot

> **Intelligent Customer Support & SLA Management Platform**  
> AI-powered ticket routing, Customer 360 intelligence, real-time SLA monitoring, and robust DSA implementations.

---

## 📌 Overview

**CloudPilot** is an enterprise-grade customer support platform designed to streamline support operations, automate ticket classification, optimize agent assignment via custom scoring and graph algorithms, and ensure stringent SLA enforcement.

---

## 🏗️ System Architecture

```
cloudpilot/
├── 🌐 frontend/       # Modern React App (Vite, Tailwind/CSS, Recharts, Lucide)
├── ☕ backend/        # Spring Boot Monolith (JPA, Redis, Security, Micrometer, Flyway)
├── 🤖 ai-service/      # Python FastAPI (Sentence-Transformers, RAG, Ticket Classifier)
├── 🗄️ database/        # SQL Schemas, Migrations & Data Models
├── 🐳 docker/          # Multi-container Compose & Dockerfiles
├── 📊 monitoring/      # Prometheus Metrics & Grafana Dashboards
└── 📚 docs/            # Architecture, API specs, and DSA documentation
```

---

## ⚡ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | React 18, Vite, React Router DOM, Axios, Recharts, Lucide Icons |
| **Backend** | Java 21, Spring Boot 3.3.x, Spring Data JPA, Spring Security, Flyway, JJWT |
| **Caching & Queue** | Redis, In-Memory Priority Queues |
| **AI & NLP** | Python 3.12, FastAPI, Sentence-Transformers, PyTorch, Scikit-Learn |
| **Database** | PostgreSQL 16 |
| **Observability** | Prometheus, Grafana, Micrometer Actuator |
| **DevOps & CI** | Docker, Docker Compose, GitHub Actions CI |

---

## 💡 Core Features & DSA Highlights

- **🧠 AI Ticket Triage & RAG**: Automated sentiment classification, category tagging, and suggested replies powered by vector retrieval.
- **⚡ Priority Queue Routing (`TicketPriorityQueue`)**: Priority-based scheduling ensuring critical SLAs are prioritized.
- **🎯 Weighted Agent Scorer (`AgentScorer`)**: Dynamic agent matching based on skill set, workload, past performance, and response latency.
- **🕸️ Service Dependency Graph (`ServiceDependencyGraph`)**: Graph traversal (BFS/DFS) to trace cascading outages and correlated ticket storms.
- **🔍 Timestamp Binary Search (`TicketTimestampSearch`)**: High-speed chronological search over resolved incident logs.
- **⏱️ Real-time SLA Monitoring**: Automated background jobs scanning open tickets with proactive escalation triggers.
- **👤 Customer 360 View**: Comprehensive profile aggregation, past order history, and AI sentiment summaries.

---

## 🚀 Getting Started

### Prerequisites

- **Node.js**: `v18+` (v20+ recommended)
- **Java**: `JDK 17+` (JDK 21 recommended)
- **Maven**: `3.9+`
- **Python**: `3.10+` (3.12 recommended)
- **Docker & Docker Compose**: (optional for containerized deployment)

---

### Local Development Setup

#### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/cloudpilot.git
cd cloudpilot
```

#### 2. Frontend
```bash
cd frontend
npm install
npm run dev
```

#### 3. Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### 4. AI Service
```bash
cd ai-service
python -m venv .venv
# On Windows:
.venv\Scripts\activate
# On Linux/macOS:
# source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

---

### 🐳 Running with Docker Compose

To spin up all services (PostgreSQL, Redis, AI Service, Backend, Frontend, Prometheus, and Grafana) with a single command:

```bash
docker compose up --build
```

---

## 📖 Documentation

- [Architecture & Design](file:///docs/architecture.png)
- [API Documentation](file:///docs/API.md)
- [DSA Algorithms & Complexity Guide](file:///docs/algorithms.md)
- [Database Design & Schema](file:///docs/database-design.md)
- [Demo & Walkthrough](file:///docs/demo.md)

---

## 🛡️ License

This project is licensed under the [MIT License](LICENSE).
