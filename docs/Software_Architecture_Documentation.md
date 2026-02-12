# Software Architecture Documentation — FraudGuard

## I. Architecture Style: Microservices

The FraudGuard system follows a Microservices Architecture in which the application is divided into small, independent, deployable services that communicate over a network. This architectural choice improves scalability, maintainability, fault tolerance, and overall performance.

### A. Justification by Granularity

#### Functional Decomposition
The system is organized around core business capabilities:
- Auth Service handles authentication, authorization, and identity management.
- Transaction Service ingests and stores financial transaction data.
- Fraud Engine Service performs real-time fraud detection using rules and risk scoring.
- Alert Service sends fraud notifications through dashboard and email channels.

#### Service Independence
Each microservice:
- Runs inside its own container.
- Contains independent business logic.
- Maintains logically isolated data storage.
- Can be deployed and scaled independently.

#### Internal Layering Inside Each Service
Every service follows a layered structure: **Controller → Service → Repository**.  
Controller handles HTTP requests and responses, Service contains business logic and fraud-processing rules, and Repository communicates with the database.  
This separation ensures clean code organization, easier testing, and long-term maintainability.

### B. Why Microservices Were Chosen

#### Scalability
The Fraud Engine Service performs computationally intensive risk scoring and real-time rule evaluation.  
By adopting a microservices architecture, this service can be scaled horizontally during peak transaction loads without requiring additional instances of the Auth, Transaction, or Alert services.  
This selective scaling optimizes infrastructure cost, improves throughput under heavy financial traffic, and ensures consistent fraud-detection performance even during sudden spikes such as sales events or payment surges.

#### Maintainability
Loose coupling between services enables independent development, testing, and deployment cycles.  
Teams can modify or enhance a single service—such as introducing SMS, push notifications, or third-party integrations in the Alert Service—without impacting authentication, transaction ingestion, or fraud-analysis logic.  
This separation reduces regression risk, simplifies debugging, and supports faster release cycles aligned with continuous integration and continuous delivery (CI/CD) practices.

#### Performance and Responsiveness
Kafka-based asynchronous event streaming allows the system to acknowledge transactions immediately at the API boundary while fraud detection and alert generation execute in parallel background workflows.  
This non-blocking design significantly reduces user-perceived latency, improves overall system responsiveness, and supports high-throughput financial environments where thousands of transactions may arrive per second.  
Caching with Redis further accelerates rule retrieval and session validation, minimizing database load and improving response times.

#### Fault Tolerance
Microservices combined with Kafka provide strong resilience against partial system failures.  
If the Alert Service or any downstream component becomes temporarily unavailable, Kafka durably stores fraud events until the service recovers, preventing data loss and ensuring eventual notification delivery.  
Service isolation also prevents cascading failures, allowing unaffected components—such as authentication and transaction ingestion—to continue operating normally.  
This design supports high availability requirements critical for financial security platforms and regulatory compliance.


## II. Application Components

### 1. Core Microservices

*API Gateway*  
Acts as the single entry point for frontend clients and manages routing, load balancing, security filtering, and rate limiting.

*Auth Service*  
Handles user registration, login, JWT authentication, and session validation.

*Transaction Service*  
Collects transactions from external systems, stores transaction history, and publishes the TransactionCreated event to Kafka.

*Fraud Engine Service*  
Consumes transaction events, applies fraud detection rules, calculates risk scores, and emits the FraudDetected event.

*Alert Service*  
Listens for fraud events, sends email and dashboard notifications, and maintains alert logs.

---

### 2. Infrastructure and Data Layer

*Kafka with Zookeeper* provides asynchronous event-driven communication, message durability, and service decoupling.

*Redis* is used for session caching, fast rule lookup, and temporary fraud-score storage.

*MySQL Cluster* persistently stores users, transactions, fraud alerts, and audit logs.

---

### 3. Frontend Component

*FraudGuard Console* is built using React and Vite and provides real-time dashboards, transaction monitoring, fraud analytics visualization, rule configuration, and alert management.

---

## III. High-Level System Flow

User → API Gateway → Auth Service → Transaction Service → Kafka → Fraud Engine Service → FraudDetected Event → Alert Service → Email or Dashboard Notification

---

## IV. Architecture Benefits Summary

The FraudGuard microservices architecture delivers independent scaling of compute-heavy services, loose coupling for rapid evolution, asynchronous processing for low latency, Kafka-based fault tolerance, and a modern real-time monitoring interface. This makes the system production-ready, resilient, and highly scalable.


---

