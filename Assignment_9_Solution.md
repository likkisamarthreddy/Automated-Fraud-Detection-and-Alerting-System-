# Assignment 9: Software Engineering Lab (Testing & Quality Assurance)

## Q1. a) Test Plan: Automated Fraud Detection and Alerting System

### 1. Objective of Testing
The primary objective is to ensure the **Fraud Detection Engine** accurately identifies suspicious transactions in real-time. The testing aims to:
- Verify that fraud rules (Amount, Velocity, Device) are correctly applied.
- Ensure risk scores are calculated accurately per rule weights.
- Validate the decision logic (Allow, Alert, Block) based on risk scores.
- Ensure the system handles high-volume data without performance degradation.

### 2. Scope
**Module under test:** Fraud Engine Service
**Included Features:**
- Rule-based detection (Amount threshold, Velocity checks, Device matching).
- Risk score capping (max 100).
- State-based decisions (ALLOW, ALERT, BLOCK).
- Integration with Kafka for transaction ingestion.

### 3. Types of Testing
- **Unit Testing:** Validating individual rules and internal logic in `RuleEngine.java`.
- **Integration Testing:** Testing the flow from `Transaction Service` -> `Kafka` -> `Fraud Engine`.
- **System Testing:** End-to-end verification from API Gateway to Alert generation.
- **Negative Testing:** Testing with invalid transaction IDs, null amounts, and disabled rules.

### 4. Tools
- **Testing Framework:** JUnit 5, Mockito
- **API Testing:** Postman / cURL
- **Monitoring:** Docker Desktop (for Kafka/Redis status), System Logs (Spring Boot)
  

### 5. Entry and Exit Criteria
- **Entry Criteria:**
  - Code for Fraud Engine and Transaction services is buildable.
  - Test database and Kafka broker are reachable.
  - Required configurations are properly set
- **Exit Criteria:**
  - All 8 designed test cases pass.
  - At least 80% code coverage on core business logic.
  - No high-severity bugs open.
  - System meets basic performance expectations under load

---

## Q1. b) Test Case Design (Module: Fraud Engine)

| Test Case ID | Test Scenario | Input Data | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-FE-001** | Normal Transaction (Low risk) | Amount: 500, Device: Known, Velocity: 1 | Risk: 0, Decision: ALLOW | Risk: 0, Decision: ALLOW | Pass (Verified) |
| **TC-FE-002** | Large Transaction (Above Limit) | Amount: 15000, Device: Known | Risk: 50, Decision: ALERT | Risk: 50, Decision: ALERT | Pass (Verified) |
| **TC-FE-003** | New Device Detection | Amount: 100, Device: "Unknown_X" | Risk: 40, Decision: ALLOW | Risk: 40, Decision: ALLOW | Pass (Verified) |
| **TC-FE-004** | Critical Risk (Multiple Rules) | Amount: 15000, Device: "Unknown_X" | Risk: 90, Decision: BLOCK | Risk: 90, Decision: BLOCK | Pass (Verified) |
| **TC-FE-005** | High Velocity (Spam) | 6 txns in < 600s (Amt: 50) | Risk: 60, Decision: ALERT | Risk: 60, Decision: ALERT | Pass (Verified) |
| **TC-FE-006** | Maximum Score Cap | Large Amount + New Device + High Velocity | Risk: 100, Decision: BLOCK | Risk: 100, Decision: BLOCK | Pass (Verified) |
| **TC-FE-007** | Boundary Value | Amount: 10000 (Exactly at limit) | Risk: 0, Decision: ALLOW | Risk: 0, Decision: ALLOW | Pass (Verified) |
| **TC-FE-008** | Rule Disabled Check | Disable 'Amount' rule, send 15000 | Risk: 0, Decision: ALLOW | Risk: 0, Decision: ALLOW | Pass (Verified) |

---

---

## Q2. a) Execution Results & Evidence: Automated Proof

### Execution Summary
Test cases were verified using an automated JUnit 5 suite (`AssignmentProofTest.java`) which mocks the Fraud Engine environment and validates decision logic across all 8 scenarios.

### Evidence (Raw Test Logs)

```text
[TC-FE-001] Score: 0, Decision: ALLOW
[TC-FE-002] Score: 50, Decision: ALERT
[TC-FE-003] Score: 40, Decision: ALLOW
[TC-FE-004] Score: 90, Decision: BLOCK
[TC-FE-005] Score: 60, Decision: ALERT
[TC-FE-006] Score: 100, Decision: BLOCK
[TC-FE-007] Score: 0, Decision: ALLOW
[TC-FE-008] Score: 0, Decision: ALLOW

[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## Q2. b) Defect Analysis (Bugs Found)

### Bug ID: BUG-001
- **Description:** Inconsistency in Velocity window between code and documentation.
- **Steps to reproduce:** Observe `RuleEngine.java` logic (60s) vs database migration comments (10 mins).
- **Expected Result:** Window should consistently be what the business requires (e.g., 10 mins).
- **Actual Result:** Hardcoded to 60,000ms (60s).
- **Severity:** Medium
- **Suggested Fix:** Externalize the velocity window time to `application.yml` or fetch from database rule config.

### Bug ID: BUG-002
- **Description:** "New Device" check logic bypassed for the first transaction.
- **Steps to reproduce:** Send a transaction for a user with 0 previous history.
- **Expected Result:** The first transaction's device should be marked as "Known" for future, but potentially flagged if suspicious.
- **Actual Result:** `!profile.isEmpty()` check causes the first transaction to always have 0 device risk, even if it's from a blacklisted device.
- **Severity:** Medium
- **Suggested Fix:** Adjust logic to only skip if it's a "First-Time-Whitelisted" scenario, or separate "New Device" from "Unknown Device".

### Bug ID: BUG-003
- **Description:** Duplicate Transaction Processing (Idempotency issue).
- **Steps to reproduce:** Send two transaction events with the same `transactionId` in rapid succession.
- **Expected Result:** System should ignore the second event or return a cached result.
- **Actual Result:** Velocity counter increments for both, potentially flagging honest users as "High Velocity" due to network retries.
- **Severity:** High
- **Suggested Fix:** Implement a Redis-based idempotency check before processing rules.
