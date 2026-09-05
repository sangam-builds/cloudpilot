# CloudPilot — Architecture Decision Records (ADR)

This document records the key architectural choices, trade-offs, and design rationales made during the development of CloudPilot.

---

## ADR-001: Spring Boot (Java 21) for Core Orchestration & SLA Engine

- **Status**: Accepted
- **Context**: The core engine must handle concurrent ticket ingestion, role-based security, stateful transactions, and dynamic background SLA breach scanning daemon loops.
- **Alternatives Considered**:
  1. *Node.js / Express*: Rapid I/O, but dynamic typing and single-threaded event loop require worker threads or external cron jobs (e.g. BullMQ/Redis) for background SLA scanning.
  2. *Python / Django or FastAPI*: Mature ORM, but lacks thread-level concurrency primitives and built-in type safety for complex multi-factor business scoring.
- **Decision**: Use **Spring Boot 3.3 on Java 21**.
- **Rationale**:
  - Strongly typed domain modeling (`records`, sealed interfaces, compile-time validation).
  - Built-in `@Scheduled` background worker for deterministic SLA deadline monitoring without external scheduler dependencies.
  - Production-grade Spring Security filter chain and Micrometer / Actuator metrics engine.

---

## ADR-002: Separate Python FastAPI Microservice for AI / NLP

- **Status**: Accepted
- **Context**: Ticket triage requires semantic vector search (RAG) and NLP classification over unstructured customer support text.
- **Alternatives Considered**:
  1. *Embedded Java Deep Learning (DJL / ONNX Runtime)*: Keeps stack unified, but limited tokenizer and library support compared to Python AI ecosystem; increases JVM memory footprint significantly.
  2. *Direct External LLM API calls from Spring Boot*: Simple, but creates external network latency dependencies on every ticket creation and incurs ongoing API token costs.
- **Decision**: Deploy a lightweight **Python FastAPI microservice** alongside Spring Boot.
- **Rationale**:
  - Native integration with Python's AI ecosystem (`sentence-transformers`, `torch`, `scikit-learn`, `pydantic`).
  - Decoupled compute: heavy vector embedding operations do not compete with JVM heap memory or garbage collection cycles.
  - Spring Boot maintains resilient keyword-fallback so backend operations continue uninterrupted if the AI container is restarting or unavailable.

---

## ADR-003: Redis for Customer 360 Caching & Session Management

- **Status**: Accepted
- **Context**: Aggregating Customer 360 metrics (lifetime order value, open/closed ticket statistics, chronological activity stream) requires multi-table joins that can become a read bottleneck under heavy traffic.
- **Alternatives Considered**:
  1. *In-Memory JVM Cache (`ConcurrentHashMap` / Caffeine)*: Zero network overhead, but cache is local to a single JVM instance and wiped upon container restart or horizontal scaling.
  2. *Direct Database Queries on Every Read*: No caching overhead, but puts redundant load on the PostgreSQL connection pool.
- **Decision**: Use **Redis** as a distributed cache layer with Spring Cache abstraction.
- **Rationale**:
  - Shared state across multiple backend replicas with configurable TTL expiration (`@Cacheable(value = "customer360", key = "#id")`).
  - Decoupled from JVM restarts; supports cloud providers (e.g. Upstash, Render Redis) with SSL encryption.

---

## ADR-004: PostgreSQL with Flyway Migrations for Relational Storage

- **Status**: Accepted
- **Context**: Support ticket lifecycle management requires ACID guarantees, foreign-key relationships (Customers, Teams, Agents, Tickets, Audit Logs), and reproducible database schema versions.
- **Alternatives Considered**:
  1. *Document Store (MongoDB)*: Flexible schema, but lacks strict relational integrity between agents, skills, and ticket audit histories.
  2. *Hibernate `hbm2ddl.auto=update`*: Automatic schema generation, but dangerous in production and lacks versioned change tracking.
- **Decision**: Use **PostgreSQL 16** managed via **Flyway incremental SQL migrations** (`db/migration/V1__...`, `V2__...`).
- **Rationale**:
  - Guaranteed referential integrity across tickets, agents, and customer transactions.
  - Immutable audit trails and deterministic seed data across local Docker, CI pipelines, and cloud databases (Neon DB).

---

## ADR-005: 40 / 30 / 20 / 10 Multi-Factor Agent Scoring Weights

- **Status**: Accepted
- **Context**: Automated ticket assignment must balance technical specialization against team workload distribution, agent CSAT ratings, and real-time availability.
- **Alternatives Considered**:
  1. *Round-Robin*: Fair workload distribution, but completely ignores technical skill specialization, routing payment issues to shipping agents.
  2. *Strict Skill-Match Only*: Routes all domain tickets to the top specialist, quickly overwhelming them while teammates sit idle.
- **Decision**: Adopt a weighted composite formula: **40% Skill Match + 30% Workload Reciprocal + 20% Performance CSAT + 10% Availability**.
- **Rationale**:
  - Prioritizes domain expertise ($0.40$) as the primary factor while preventing burnout through harmonic load balancing ($\frac{1}{1 + \text{workload}}$ with $0.30$ weight).
  - CSAT rating ($0.20$) and live availability ($0.10$) act as quality and presence tie-breakers.

---

## ADR-006: Deliberate Scope Simplifications for Academic Delivery

- **Status**: Accepted
- **Context**: Delivering a clean, defensible project within academic semester constraints required prioritizing architectural clarity over unnecessary operational infrastructure.
- **Decisions & Scope Cuts**:
  1. *Graph Topology*: Implemented in-memory adjacency list with BFS/DFS in Java instead of deploying and managing a dedicated Neo4j graph database cluster.
  2. *Vector Embeddings*: In-memory cosine similarity over local pre-computed FAQ vectors (`all-MiniLM-L6-v2`) instead of deploying a separate vector database cluster (e.g., Milvus or Pinecone).
  3. *Message Broker*: Spring in-process scheduler and REST communication instead of Apache Kafka cluster for ticket events, minimizing local Docker memory requirements (< 1.5GB total).
