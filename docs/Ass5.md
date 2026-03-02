# CS 331 (Software Engineering Lab) - Assignment 5 Solution

**Project Title:** Automated Fraud Detection and Alerting System


## III. Implementation of Application Components & Interactions

We deploy a highly decoupled microservices architecture where services primarily interact asynchronously via an Apache Kafka event bus. This prevents bottlenecks and ensures the system remains responsive under heavy load. Below is the implementation trace demonstrating the interaction between two core components: the **Transaction Service** and the **Fraud Engine**.

### 1. The Transaction Service Component
This component exposes REST APIs to ingest telemetry. Rather than performing blocking validations, it writes the transaction to the database in a "Pending" state and acts as a **Producer**. It serializes the transaction object into JSON and publishes it to the `transactions` Kafka topic.

### 2. The Fraud Engine Component
This is a background processing daemon with no public endpoints. It acts as a **Consumer**. It continuously polls the `transactions` Kafka topic. Upon receiving data, it runs the transaction payload against a configured rule engine (e.g., velocity checks, high-amount thresholds). It then assumes the role of a Producer, publishing its final verdict (Allow, Block, Flag) to the `fraud-results` topic.

### Interaction Trace (Component Log Output)
The following pseudo-logs demonstrate the precise point of interaction between the two components after a user submits a $5000 transaction.

1.  **[Transaction Service]** `[INFO] Received POST /api/transactions for User ID: U123`
2.  **[Transaction Service]** `[INFO] Persisted TXN-89412 to MySQL. Status: PENDING.`
3.  **[Transaction Service]** `[DEBUG] Producer: Published TXN-89412 to Kafka topic [transactions]`
4.  *(Component Interaction Occurs over Event Bus)*
5.  **[Fraud Engine]** `[DEBUG] Consumer: Polled 1 record from topic [transactions]`
6.  **[Fraud Engine]** `[INFO] Executing Rule Suite on TXN-89412...`
7.  **[Fraud Engine]** `[WARN] Rule Violation: Transaction Amount ($5000) exceeds configured threshold.`
8.  **[Fraud Engine]** `[DEBUG] Producer: Published BLOCKED verdict for TXN-89412 to topic [fraud-results]`
9.  *(Component Interaction Resolves)*
10. **[Transaction Service]** `[DEBUG] Consumer: Polled 1 record from topic [fraud-results]`
11. **[Transaction Service]** `[INFO] Updated state of TXN-89412 to BLOCKED in database.`
