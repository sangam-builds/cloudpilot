# CloudPilot — Database Architecture & Schema Design

CloudPilot is designed to run against enterprise-grade cloud PostgreSQL instances (e.g. **Neon**, **Supabase**, **Render**, or **AWS RDS**) with SSL encryption, Flyway schema migrations, and HikariCP connection pooling.

---

## 📐 Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    CUSTOMERS ||--o{ ORDERS : places
    CUSTOMERS ||--o{ TICKETS : opens
    TEAMS ||--o{ AGENTS : contains
    AGENTS ||--o{ TICKETS : assigned_to
    TICKETS ||--o{ TICKET_HISTORY : has
    FAQS {
        bigserial id PK
        varchar category
        text question
        text answer
        timestamptz created_at
    }
    AUDIT_LOGS {
        bigserial id PK
        varchar actor_id
        varchar actor_role
        varchar action
        varchar entity_type
        varchar entity_id
        text details
        timestamptz created_at
    }
    CUSTOMERS {
        bigserial id PK
        varchar name
        varchar email UK
        varchar phone
        timestamptz created_at
    }
    ORDERS {
        bigserial id PK
        bigint customer_id FK
        decimal amount
        varchar status
        timestamptz created_at
    }
    TEAMS {
        bigserial id PK
        varchar name UK
        text description
        timestamptz created_at
    }
    AGENTS {
        bigserial id PK
        varchar name
        varchar email UK
        bigint team_id FK
        text skill_tags
        decimal rating
        boolean is_available
        integer current_workload
        timestamptz created_at
    }
    TICKETS {
        bigserial id PK
        bigint customer_id FK
        varchar subject
        text description
        varchar category
        varchar priority
        varchar sentiment
        varchar status
        bigint assigned_agent_id FK
        timestamptz sla_deadline
        varchar risk_status
        timestamptz created_at
        timestamptz updated_at
    }
    TICKET_HISTORY {
        bigserial id PK
        bigint ticket_id FK
        varchar from_status
        varchar to_status
        varchar changed_by
        timestamptz changed_at
    }
```

---

## 🗄️ Indexing & Performance Strategy

| Table | Index Columns | Type | Purpose |
|---|---|---|---|
| `tickets` | `(status)` | B-Tree | High-speed filtering on active vs closed tickets |
| `tickets` | `(priority)` | B-Tree | PriorityQueue queries and triage analytics |
| `tickets` | `(customer_id)` | B-Tree | Instant Customer 360 relation lookups |
| `tickets` | `(risk_status, sla_deadline)` | Composite | Real-time SLA monitor scanning and alerting |
| `audit_logs` | `(entity_type, entity_id)` | Composite | Fast audit trail retrieval per ticket or customer |
| `agents` | `(team_id, is_available)` | Composite | Rapid candidate filtering for agent scoring |

---

## 🔄 Ticket Status State Machine

```mermaid
stateDiagram-v2
    [*] --> NEW: Customer Creation
    NEW --> ASSIGNED: Live Agent Scorer
    NEW --> IN_PROGRESS: Direct Claim
    ASSIGNED --> IN_PROGRESS: Agent Acknowledges
    IN_PROGRESS --> PENDING_CUSTOMER: Awaiting Info
    PENDING_CUSTOMER --> IN_PROGRESS: Customer Replies
    IN_PROGRESS --> RESOLVED: Fix Delivered
    RESOLVED --> CLOSED: Auto-Close (48h)
    RESOLVED --> IN_PROGRESS: Customer Reopens
    CLOSED --> NEW: Incident Reopened
```

---

## ☁️ Cloud Database Configuration

To connect CloudPilot to a cloud PostgreSQL provider, configure the datasource environment variables:

```properties
# Cloud Connection String with SSL
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-silent-pool-12345.us-east-2.aws.neon.tech/cloudpilot?sslmode=require
SPRING_DATASOURCE_USERNAME=cloudpilot_owner
SPRING_DATASOURCE_PASSWORD=your_secure_cloud_password

# Flyway Migration Controls
SPRING_FLYWAY_ENABLED=true
SPRING_FLYWAY_BASELINE_ON_MIGRATE=true
```
Flyway automatically applies `V1__init_schema.sql` and `V2__seed_data.sql` during Spring Boot startup.
