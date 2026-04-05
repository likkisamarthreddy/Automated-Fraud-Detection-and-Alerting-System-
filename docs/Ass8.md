## Part A: Data Access Layer (DAL) Implementation 

The *Data Access Layer (DAL)* is a crucial component in software application architecture that serves as an abstraction layer between the application logic and the database. Our Automated Fraud Detection and Alerting System uses a *microservice-based database architecture* where each service owns its own schema, implemented using *Spring Data JPA* with *Flyway* for version-controlled migrations.

### 1. Database Creation and Table Schemas

Our system relies on isolated databases to ensure independent scaling and reduced coupling between microservices. 

*Database Initialisation Overview:*
During the bootstrapping phase, separate schemas are created and initialised with strict access privileges. The system provisions the following databases:
*   ⁠ fraud_auth ⁠: Manages user credentials and roles.
*   ⁠ fraud_transactions ⁠: Stores all incoming transaction attempts.
*   ⁠ fraud_engine ⁠: Stores the configurable fraud rules and the resulting evaluation decisions.
*   ⁠ fraud_alerts ⁠: Manages flagged incidents and tracks analyst review statuses.

*Table Schema Overview (Managed via Flyway Migrations):*
*   *⁠ fraud_results ⁠ Table:* Contains records of every transaction evaluated by the rule engine, storing the raw ⁠ amount ⁠, ⁠ device ⁠ ID, resulting ⁠ risk_score ⁠, and the final ⁠ decision ⁠ (ALLOW, ALERT, or BLOCK).
*   *⁠ fraud_rules ⁠ Table:* Manages the dynamic business rules such as ⁠ threshold_value ⁠ and ⁠ risk_score_weight ⁠. Contains seeded default rules for velocity, amount, and device checks.
*   *⁠ alerts ⁠ Table:* Stores flagged transactions mapped to their ⁠ alert_id ⁠ with an operational ⁠ status ⁠ (OPEN, BLOCKED, ALLOWED, UNDER_REVIEW) that analysts interact with.
*   *⁠ transactions ⁠ Table:* Logs incoming raw events, linking a unique ⁠ transaction_id ⁠ to a ⁠ user_id ⁠ alongside device metadata and timestamps.
*   *⁠ users ⁠ Table:* Secures the platform by maintaining system usernames, hashed passwords, and assigned roles (e.g., ANALYST).

### 2. DAL Code Components

We implemented our DAL using the Java Persistence API (JPA) standards provided by Spring Boot. This approach abstracts SQL queries, allowing business logic to interact with native Java objects.

**2.1 Data Entities (Object-Relational Mapping)**
Entities serve as the direct bridge mapping database rows to Java classes using Jakarta persistence annotations:
*   **`FraudResult` Entity:** Maps directly to `fraud_results`. Enforces constraints such as unique transaction IDs and sets audit timestamps upon insertion natively.
*   **`FraudRule` Entity:** Maps to `fraud_rules`, allowing the system to update active/inactive rules dynamically without executing raw SQL `UPDATE` statements.
*   **`Alert` Entity:** Translates an alerted state. Default values like "OPEN" are applied automatically via lifecycle callbacks before persistence.
*   **`Transaction` & `User` Entities:** Map base system inputs and security controls, seamlessly translating column-types (like SQL `DECIMAL` to Java `BigDecimal`).

**2.2 JPA Repositories (DAL Interfaces)**
The repository layer handles database I/O, supporting CRUD (Create, Read, Update, Delete) without needing manual SQL construction:
*   **`FraudResultRepository` & `FraudRuleRepository`:** Exposes methods to retrieve enabled rules (`findByEnabledTrue`) and find fraud logs by their unique transaction keys.
*   **`AlertRepository`:** Supports pagination for returning dashboard data to analysts and uses derived query methods (e.g., `findByStatus`) to filter open alerts heavily.
*   **`TransactionRepository`:** Implements `@Query` annotations to provide complex querying with multiple optional filters (userId, risk decision, custom date ranges) to power historical search panels.
*   **`UserRepository`:** Provides streamlined lookups like `findByUsername` to power Spring Security's authentication layer.

**2.3 Service Layer Integration Integration**
The Service Layer utilizes our DAL repositories under strict atomicity guarantees via the `@Transactional` annotation. For performance efficiency, repetitive DAL reads (like loading active `FraudRules`) are heavily cached, and the caches automatically evict upon any write operations to the rule repository.

---
