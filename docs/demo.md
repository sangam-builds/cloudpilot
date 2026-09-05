# CloudPilot — Live Demonstration Script & Evaluator Guide

This guide provides a rapid 5-minute walkthrough of CloudPilot's core capabilities for evaluators, judges, and developers.

> 🌐 **Live Web Demo**: [https://cloudpilot-frontend-qfbwl7ncz-sangams-projects-d081cefb.vercel.app/login](https://cloudpilot-frontend-qfbwl7ncz-sangams-projects-d081cefb.vercel.app/login) (No local setup required)

---

## 🚀 Quick Launch (Single Command)

```bash
docker compose up --build
```
- **Frontend Dashboard**: `http://localhost:3000` (or `http://localhost:5173` via Vite)
- **Spring Boot Backend**: `http://localhost:8080`
- **FastAPI AI Engine**: `http://localhost:8000/docs`
- **Prometheus Metrics**: `http://localhost:9090`
- **Grafana Dashboards**: `http://localhost:3001` (login: `admin` / `admin`)

---

## 👤 Step 1: Authentication & Role Switcher

1. Open `http://localhost:3000`.
2. Click any of the **Demo Quick-Select Accounts**:
   - **Admin**: `admin@cloudpilot.io` (password: `password123`)
   - **Agent**: `alex@cloudpilot.io` (password: `password123`)
   - **Customer**: `support@acmecorp.com` (password: `password123`)
3. Notice the **Role Switcher** in the top navbar: you can toggle between **Customer**, **Agent**, and **Admin** on the fly.

---

## ⚡ Step 2: Live AI NLP Triage & Auto-Assignment

1. Navigate to the **Tickets & Triage** page (`/tickets`).
2. Click any of the preset scenario buttons under **Create New Support Ticket**:
   - `💳 Double Billing Issue`: Triggers **Payments** classification, **HIGH** priority (2h SLA), **FRUSTRATED** sentiment, auto-assigns to payment specialist *Alex Mercer*.
   - `🚨 Production API Outage`: Triggers **Technical Support**, **HIGH** priority, auto-assigns to tech engineer *David Chen*.
   - `📦 Customs Delivery Delay`: Triggers **Shipping & Logistics**, auto-assigns to shipping agent *Marcus Vance*.
3. Click **Submit Ticket & Run AI Pipeline**.
4. Observe the instant green result card revealing:
   - NLP category & confidence score
   - Calculated SLA deadline countdown timer
   - Assigned engineer with multi-factor scoring breakdown

---

## 🧠 Step 3: RAG Knowledge Base Draft Reply

1. In the ticket feed, click on the newly created ticket.
2. In the right pane, look at the **AI Copilot Grounded Draft Reply (RAG)** card.
3. The AI service searched the embedded FAQ index via cosine vector similarity and generated an accurate, polite resolution draft.
4. Click **Use This Draft** — it automatically populates the response input!
5. Click **Send** to post the message to the ticket thread.

---

## 🔍 Step 4: Customer 360 View

1. Navigate to **Customer 360** (`/customer/1`).
2. Review the consolidated intelligence:
   - **Lifetime Spend**: Total order volume across all historical transactions ($25,400+).
   - **AI Intelligence Summary**: Synthesized natural language summary of relationship health.
   - **Chronological Timeline**: Unified vertical timeline interleaving orders and support tickets.
3. Use the **Switch Profile** dropdown to explore other enterprise accounts.

---

## 🛡️ Step 5: SLA At-Risk Alerts & Blast Radius Simulator

1. Navigate to **Admin & SLA Console** (`/admin`).
2. Notice the live **SLA Alert Banner** highlighting tickets with $\le 20\%$ deadline remaining or breached status.
3. Scroll to **Service Dependency Graph & Blast Radius**:
   - Select `Payment Service` from the dropdown.
   - Click **Run Traversal**.
   - Watch the BFS/DFS graph algorithm calculate downstream affected dependencies (`Order Service`, `Fulfillment Service`, `Shipping Logistics`, `Notification Service`).
4. Review the **Agent Workload Matrix** and toggle an agent's availability to `Unavailable`. Subsequent tickets will immediately bypass this agent!
5. Inspect the **Immutable Compliance & Security Audit Trail** for SOC2 change records.
