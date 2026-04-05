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

 ## Part B: White Box and Black Box Testing [20 Marks]

Our testing methodology involves splitting verification between internal logic pathways (White Box) and external interface validation (Black Box).

### 1. White Box Testing (Glass Box Testing) [10 Marks]

White box testing targets internal code paths, branches, algorithms, and conditions. Our White Box test suites directly mock external dependencies to isolate and verify business logic mathematically.

**Suite 1: Internal Rule Engine Algorithms (`RuleEngineTest`)**
*   **Path Checked (Rule Threshold Limit Exceeded):** Evaluated transactions where the amount exceeded a configured threshold, verifying that the calculation branch successfully increments the risk score appropriately.
*   **Path Checked (Conditions Skipped / Safe):** Validated that a transaction falling cleanly below the necessary alert thresholds safely bypasses failure conditions, outputting an empty risk score (0) and an `ALLOW` decision.
*   **Path Checked (Unknown Variables):** Checked the condition branch handling unknown device signatures. We verified the engine accurately flags new parameters without throwing exceptions.

**Suite 2: Database Persistence & Messaging Logic (`FraudEngineServiceTest`)**
*   **Idempotency Guards:** Triggered a duplicate transaction attempt. Verified that internal `if-early-return` logic successfully suppresses duplicate DAL writes entirely.
*   **Mock Verification:** Captured the outgoing objects directly immediately before they hit the repository DAL layer to ensure field manipulations (such as calculated risk scores) executed correctly.

**Suite 3: Throttle Conditional Logic (`AlertServiceTest`)**
*   **Path Checked (Alert Overloading):** We mocked the database to report an artificially high alert count for a specific user to ensure our internal 'circuit-breaker' successfully bypassed alert creation logic and throttled output.

### 2. Black Box Testing (Functional Testing) [10 Marks]

Black box testing assesses the fully booted system through its exposed public boundaries, treating the software strictly through input and observable output.

**Suite 1: Fraud Rule API Exposure (`FraudRuleControllerTest`)**
*   **Endpoint Verified (Read Access):** Hit standard `GET` requests against the rules framework to verify that underlying DB contents correctly translate to JSON lists equipped with HTTP 200 OK statuses.
*   **Endpoint Verified (Write Commands):** Fired parameterized POST payloads specifying rule data, asserting that the system successfully consumes the JSON format and translates it down to a 201 Created state successfully.

**Suite 2: Alert Modification API Exposure (`AlertControllerTest`)**
*   **Endpoint Verified (Paginated Discovery):** Requested `GET /api/alerts` payloads. Asserted the structure matches expected paginated layouts, ensuring UI dashboards can parse the results naturally.
*   **Endpoint Verified (State Mutation):** Pushed `PUT` updates against specific generic IDs attempting to transition them to 'BLOCKED'. Confirmed the expected JSON representations mutated accordingly.
*   **Endpoint Verified (Analyst Metrics):** Retrieved broad metric aggregation payloads checking that the system aggregates broad database statistics without exposing internal variables correctly.

### 3. Testing Summary Table
The table below maps out all specific tests designed to satisfy both White and Black box requirements.

| # | Test Type | Test File / Target Component | Goal / Path Checked | Result |
|---|-----------|-----------------------------|---------------------|--------|
| 1 | White Box | `RuleEngineTest` | Verify score calculation path when amount > threshold | PASSED |
| 2 | White Box | `RuleEngineTest` | Verify ALLOW decision calculation when amount < threshold | PASSED |
| 3 | White Box | `RuleEngineTest` | Verify unknown device triggers correct scoring calculations | PASSED |
| 4 | White Box | `FraudEngineServiceTest` | Verify early-return branch for idempotency guards | PASSED |
| 5 | White Box | `FraudEngineServiceTest` | Verify DAL `FraudResult` persistence parameters | PASSED |
| 6 | White Box | `FraudEngineServiceTest` | Verify messaging logic triggered exactly once | PASSED |
| 7 | White Box | `AlertServiceTest` | Verify successful alert creation database pathways | PASSED |
| 8 | White Box | `AlertServiceTest` | Verify throttle guard branch skips database writes | PASSED |
| 9 | White Box | `AlertServiceTest` | Verify `resolvedAt` timestamp injection on status mutations | PASSED |
| 10| Black Box | `FraudRuleControllerTest` | Verify list payload via `GET /api/rules` | PASSED |
| 11| Black Box | `FraudRuleControllerTest` | Verify creation payloads via `POST /api/rules` | PASSED |
| 12| Black Box | `AlertControllerTest` | Verify paginated data exposure via `GET /api/alerts` | PASSED |
| 13| Black Box | `AlertControllerTest` | Verify external update commands via `PUT /api/alerts/{id}/block` | PASSED |
| 14| Black Box | `AlertControllerTest` | Verify metrics aggregation JSON body via `GET /api/alerts/metrics` | PASSED |

### 4. Code Execution Evidence (Terminal Output Logs)

The following logs prove the successful native execution of all tests spanning our separated domain modules:

**Engine Service Test Execution Evidence:**
```text
[INFO] Scanning for projects...
[INFO] ----------------< com.frauddetection:fraud-engine-service >---------------
[INFO] --- maven-surefire-plugin:3.1.2:test (default-test) @ fraud-engine-service ---
[INFO] Running com.frauddetection.fraudengine.engine.RuleEngineTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in RuleEngineTest
[INFO] Running com.frauddetection.fraudengine.service.FraudEngineServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in FraudEngineServiceTest
[INFO] Running com.frauddetection.fraudengine.rest.FraudRuleControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in FraudRuleControllerTest
[INFO]
[INFO] Results:
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Alert Service Test Execution Evidence:**
```text
[INFO] Scanning for projects...
[INFO] ----------------< com.frauddetection:alert-service >---------------
[INFO] --- maven-surefire-plugin:3.1.2:test (default-test) @ alert-service ---
[INFO] Running com.frauddetection.alert.service.AlertServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in AlertServiceTest
[INFO] Running com.frauddetection.alert.rest.AlertControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in AlertControllerTest
[INFO]
[INFO] Results:
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
