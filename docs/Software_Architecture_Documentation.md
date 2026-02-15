#  Automated Fraud Detection & Alerting System

---

#  Software Architecture

## I. Architecture Style: Microservices Architecture

---

## A. Justification by Component Granularity

The system follows a **Microservices Architecture** because each backend component is:

- Independently deployable  
- Independently scalable  
- Running on a separate port  
- Packaged as a separate Docker container  
- Owning its own database schema  

### 🔹 Service Granularity

| Microservice | Port | Database | Responsibility |
|--------------|------|----------|----------------|
| auth-service | 8081 | fraud_auth | Authentication & JWT management |
| transaction-service | 8082 | fraud_transactions | Transaction ingestion & persistence |
| fraud-engine-service | 8083 | fraud_engine | Risk scoring & rule evaluation |
| alert-service | 8084 | fraud_alerts | Alert generation & management |
| api-gateway | 8080 | — | Routing, JWT validation & rate limiting |

---

### 🔹 Why This Is Microservices (Not Monolithic or SOA)

- **Database-per-service pattern**  
  Each service owns its schema (fraud_auth, fraud_transactions, etc.).

- **Independent deployment**  
  Each service:
  - Has its own Maven module  
  - Has its own Dockerfile  
  - Runs as a separate container  

- **Asynchronous communication**  
  Services communicate via **Apache Kafka topics**, not shared in-process method calls.

- **Loose coupling**  
  Only lightweight DTOs are shared via the `common` module.

- **API Gateway pattern**  
  A dedicated gateway handles routing and authentication instead of a centralized ESB (as in SOA).

---

## B. Why Microservices Is the Best Choice

| Quality Attribute | Justification |
|-------------------|--------------|
| **Scalability** | Fraud engine (CPU-intensive) can scale independently without scaling auth-service. |
| **Maintainability** | Small, focused codebases per service. Easier debugging and isolated updates. |
| **Performance** | Kafka enables asynchronous processing; Redis reduces latency via caching. |
| **Fault Isolation** | If alert-service fails, transactions still flow through Kafka without system-wide failure. |
| **Technology Flexibility** | Backend uses Spring Boot (Java); frontend uses React + TypeScript. Services evolve independently. |
| **Deployment** | Docker Compose orchestrates all services; easily migratable to Kubernetes. |

---

# II. Application Components

---

## 🔹 Backend Microservices

### 1️⃣ API Gateway (`api-gateway`)

- Single entry point for client requests  
- JWT token validation  
- Route forwarding  
- Redis-based rate limiting  

---

### 2️⃣ Auth Service (`auth-service`)

- User registration  
- User login  
- JWT generation & validation  
- Password hashing (BCrypt)  
- Role management  

---

### 3️⃣ Transaction Service (`transaction-service`)

- Create & retrieve financial transactions  
- Persist transactions in MySQL  
- Publish transaction events to Kafka  

---

### 4️⃣ Fraud Engine Service (`fraud-engine-service`)

- Consume transaction events from Kafka  
- Apply rule-based scoring algorithms  
- Perform velocity checks using Redis  
- Compute risk score  
- Decide: **ALLOW / ALERT / BLOCK**  
- Publish fraud-alert events  

---

### 5️⃣ Alert Service (`alert-service`)

- Consume fraud-alert events  
- Persist alerts in database  
- Manage alert status (Active, Resolved, Escalated)  
- Provide alert querying APIs  

---

### 6️⃣ Common Library (`common`)

- Shared DTOs  
- Enums  
- Utility classes  
- Prevents duplication across services  

---

## 🔹 Infrastructure Components

| Component | Role |
|-----------|------|
| **MySQL 8.0** | Persistent storage (separate schema per service) |
| **Redis 7** | Caching, velocity tracking, rate limiting |
| **Apache Kafka** | Asynchronous event communication |
| **Zookeeper** | Kafka coordination |
| **Docker Compose** | Container orchestration |

---

## 🔹 Frontend Application (`fraudguard-console`)

Built with:

- React  
- TypeScript  
- Vite  

### Pages

- Landing  
- Login  
- Dashboard  
- Transactions  
- Alerts  
- Analytics  
- Rule Management  
- System Health  
- Simulation  

### Service Modules

- `authService.ts`  
- `transactionService.ts`  
- `alertService.ts`  
- `ruleService.ts`  

All frontend services communicate via the **API Gateway**.

---

#  Final Summary

The system follows a Microservices Architecture where independently deployable services communicate asynchronously through Kafka, each owning its database. This ensures scalability, maintainability, fault isolation, and high performance in a real-time fraud detection environment.
