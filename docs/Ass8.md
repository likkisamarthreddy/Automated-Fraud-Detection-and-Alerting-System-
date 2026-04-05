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
