# CloudPilot — REST API Reference

The CloudPilot backend exposes a comprehensive set of RESTful endpoints secured with JWT Bearer authentication. When running locally, interactive Swagger UI documentation is available at `http://localhost:8080/swagger-ui.html`.

---

## 🔐 Authentication & Session Endpoints

### 1. User Registration
- **URL**: `POST /api/auth/register`
- **Auth**: Public
- **Request Body**:
```json
{
  "name": "Apex Corporation",
  "email": "ops@apexcorp.com",
  "password": "password123"
}
```
- **Response (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "userId": 1,
  "email": "ops@apexcorp.com",
  "name": "Apex Corporation",
  "role": "CUSTOMER"
}
```

### 2. User / Agent Login
- **URL**: `POST /api/auth/login`
- **Auth**: Public
- **Request Body**:
```json
{
  "email": "alex@cloudpilot.io",
  "password": "password123"
}
```
- **Response (200 OK)**: Returns tokens, role (`ADMIN`, `AGENT`, or `CUSTOMER`), and user identity metadata.

---

## 🎫 Ticket Lifecycle & Triage Endpoints

### 1. Create & Auto-Triage Ticket
Triggers the full AI classification pipeline (NLP category, priority, sentiment), SLA deadline math, and weighted agent scoring.
- **URL**: `POST /api/tickets`
- **Auth**: `Bearer <token>`
- **Request Body**:
```json
{
  "customerId": 1,
  "subject": "Double charge of $7,800 on monthly subscription",
  "description": "Our card was charged twice on invoice #9481. Need immediate refund for the duplicate transaction."
}
```
- **Response (201 Created)**:
```json
{
  "id": 13,
  "customerId": 1,
  "customerName": "Acme Corporation",
  "customerEmail": "support@acmecorp.com",
  "subject": "Double charge of $7,800 on monthly subscription",
  "description": "Our card was charged twice on invoice #9481. Need immediate refund for the duplicate transaction.",
  "category": "Payments",
  "priority": "HIGH",
  "sentiment": "FRUSTRATED",
  "status": "ASSIGNED",
  "assignedAgentId": 1,
  "assignedAgentName": "Alex Mercer",
  "assignedTeamName": "Payments & Billing",
  "slaDeadline": "2026-09-04T02:00:00Z",
  "remainingMinutes": 120,
  "riskStatus": "ON_TRACK",
  "createdAt": "2026-09-04T00:00:00Z",
  "updatedAt": "2026-09-04T00:00:00Z"
}
```

### 2. Query & Filter Tickets
- **URL**: `GET /api/tickets`
- **Query Parameters**:
  - `status`: Filter by `NEW`, `ASSIGNED`, `IN_PROGRESS`, `PENDING_CUSTOMER`, `RESOLVED`, `CLOSED`.
  - `priority`: Filter by `HIGH`, `MEDIUM`, `LOW`.
  - `customerId`: Filter by customer account ID.
  - `agentId`: Filter by assigned support engineer ID.
  - `page`: Page index (default: `0`).
  - `size`: Items per page (default: `10`).

### 3. Update Status Transition
Enforces the strict status state machine transition rules.
- **URL**: `PATCH /api/tickets/{id}/status`
- **Request Body**:
```json
{
  "status": "IN_PROGRESS"
}
```

### 4. Get RAG Suggested Reply
- **URL**: `GET /api/tickets/{id}/suggest-reply`
- **Response (200 OK)**:
```json
{
  "suggestedReply": "Thank you for reaching out to CloudPilot Support. We have verified the duplicate billing transaction on invoice #9481 and initiated a full credit reversal of $7,800 to your original payment method. Funds will reflect within 3–5 business days."
}
```

---

## 👤 Customer 360 Endpoints

### 1. Consolidated Customer 360 Profile
- **URL**: `GET /api/customers/{id}/360`
- **Response (200 OK)**:
```json
{
  "customerId": 1,
  "name": "Acme Corporation",
  "email": "support@acmecorp.com",
  "phone": "+1-555-0101",
  "customerSince": "2025-01-15T08:00:00Z",
  "totalOrders": 3,
  "totalSpend": 25400.00,
  "openTicketsCount": 1,
  "resolvedTicketsCount": 2,
  "aiSummary": "Acme Corporation is an Enterprise client with $25,400 in lifetime spend across 3 transactions. They have 1 active high-priority billing inquiry and 2 resolved technical tickets. Overall account health is strong.",
  "recentActivity": [
    {
      "type": "TICKET",
      "id": 13,
      "title": "Double charge of $7,800 on monthly subscription",
      "status": "ASSIGNED",
      "amount": null,
      "timestamp": "2026-09-04T00:00:00Z"
    },
    {
      "type": "ORDER",
      "id": 1,
      "title": "Order #1",
      "status": "COMPLETED",
      "amount": 12500.00,
      "timestamp": "2026-08-20T10:30:00Z"
    }
  ]
}
```

---

## 📊 Admin Analytics & SLA Endpoints

### 1. Operational Metrics Summary
- **URL**: `GET /api/metrics/summary`
- **Response (200 OK)**: Returns aggregate counters, SLA compliance rate, queue length, and status breakdown.

### 2. Service Dependency Blast Radius Simulator
- **URL**: `GET /api/metrics/blast-radius?failedService=Payment+Service`
- **Response (200 OK)**:
```json
{
  "failedService": "Payment Service",
  "affectedServicesBFS": ["Order Service", "Fulfillment Service", "Shipping Logistics", "Notification Service"],
  "affectedServicesDFS": ["Order Service", "Fulfillment Service", "Shipping Logistics", "Notification Service"],
  "impactedCount": 4
}
```

### 3. At-Risk SLA Feed
- **URL**: `GET /api/sla/at-risk`
- **Response (200 OK)**: Returns all tickets currently flagged as `AT_RISK` (&le; 20% SLA remaining) or `BREACHED`.
