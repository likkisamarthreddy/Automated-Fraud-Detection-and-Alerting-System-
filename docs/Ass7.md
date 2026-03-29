# CS 331 (Software Engineering Lab) - Assignment 7 Solution

## Q1. Core Functional Modules Related to the Business Logic Layer

In our Automated Fraud Detection and Alerting System, the Business Logic Layer (BLL) serves as the core evaluation and processing hub. The following key modules have been implemented:

1. **Rule Engine Module (`RuleEngine.java`)**: 
   - This is the core component that evaluates incoming transactions against active fraud rules. It calculates a risk score based on three types of rules: `AMOUNT` (threshold limit), `VELOCITY` (transaction frequency), and `DEVICE` (unrecognized/new devices). 
   - Depending on the cumulative score, it generates a decision: `ALLOW`, `ALERT`, or `BLOCK`.

2. **Fraud Engine Service (`FraudEngineService.java`)**:
   - The coordinator module that consumes transaction events from Kafka, interacts with the Idempotency service to avoid duplicate processing, invokes the `RuleEngine`, saves the `FraudResult` to the database, and publishes the result back for the Alert and Transaction services.

3. **Velocity Service (`VelocityService.java`)**:
   - Maintains and retrieves the user profile and recent transaction history. It tracks known devices and provides the necessary context (e.g., transactions in the last 60 seconds) for the `RuleEngine` to evaluate velocity rules.

4. *Transaction Service (TransactionService.java)*:
   - Acts as the entry point for new transactions from the user. It handles the initial logging of the transaction, assigning a UUID, getting the server timestamp, saving the raw transaction state, and publishing the TransactionEvent.

### Interaction with the Presentation Layer

The presentation layer (React-based Dashboard UI) interacts with the BLL through REST API endpoints exposed by the controllers:
- *Transaction Submission*: The UI calls the POST /api/transactions endpoint (via TransactionController). The controller passes the request to the TransactionService (BLL) which processes it and responds.
- *Rule Configuration*: The UI components used by administrators fetch, create, or update fraud rules via FraudRuleController, which bridges the UI directly to the FraudRuleService for enabling/disabling system rules dynamically.
- *Alert Triage*: Analysts interact with the UI to view alerts. The UI calls AlertController which retrieves data from the AlertService. Any action taken by analysts is processed back through the BLL to update system states.

---

## Q2. Software Engineering Project Descriptions

### A) Implementation of Business Rules

Business rules in our system define the conditions under which a transaction is flagged as fraudulent. They are implemented dynamically and evaluated by the RuleEngine module:
- *AMOUNT Rules*: The system checks if the individual transaction amount exceeds a specified threshold configured by the admin (e.g., > $1000).
- *VELOCITY Rules*: The system aggregates the number of transactions performed by a specific user within a short timeframe (e.g., the last 60 seconds via VelocityService). If the frequency exceeds a threshold (e.g., > 3 transactions per minute), the rule is triggered.
- *DEVICE Rules*: The system checks the user's transaction profile to see if the device ID matches previously known devices. If a transaction originates from a new device, it triggers this rule.
Each triggered rule adds a weighted "Risk Score" to the transaction. Finally, logic applies a threshold decision: Score >= 80 (BLOCK), Score >= 50 (ALERT), and everything else (ALLOW).

### B) Validation Logic

Yes, validation logic is implemented rigorously to ensure data integrity before any processing begins. The system uses **Jakarta Bean Validation** (e.g., `@Valid`, `@NotBlank`, `@Positive`) primarily at the controller level:

- **Incoming Payload Validation**: When the presentation layer (UI) sends a `POST` request to create a transaction, the JSON payload is automatically mapped to a `TransactionRequest` DTO by Spring Boot's HTTP message converters.
- **Rules Verified**: 
  - `@NotBlank(message = "userId is required")` ensures the user ID is present and not empty.
  - `@Positive(message = "amount must be positive")` prevents negative or zero-value transactions, ruling out logically impossible business events right at the boundary.
  - `@NotBlank(message = "device is required")` ensures a device identifier is always provided (critical for the Device rules in the BLL).
- **Error Handling & API Boundaries**: If the incoming data violates these annotations, Spring Boot automatically intercepts the request. The validation throws a `MethodArgumentNotValidException`. We also use a unified `ApiResponse` format and exception handlers in the Gateway and Services to capture these validation failures, returning an immediate, structured HTTP `400 Bad Request` to the presentation layer without polluting the Business Logic Layer. This keeps the core logic free of "bad data" checks.

### C) Data Transformation

Data transformation is heavily utilized across our application ecosystem to decouple backend domain structures from both the presentation layer (UI) and other microservices. Key transformation patterns in our system include:

1. **Presentation Layer Transformations (DTO mapping)**: 
   - Data received from the UI is mapped to Request/Data Transfer Objects (DTOs), such as `TransactionRequest`. This prevents exposing internal database structures or giving clients direct access to mutable entities.
   - For outward responses, internal data sets (like a `Page<Transaction>`) are transformed and wrapped in an `ApiResponse.success(data)` payload. This guarantees the UI always receives a consistent, standardized JSON envelope containing status, messages, and paginated data, rather than raw database schemas.

2. **Service-Level Transformation (DTO to Entity)**: 
   - In the `TransactionService`, the incoming `TransactionRequest` DTO is explicitly transformed into a `Transaction` Domain Entity.
   - During this transformation, the service layer evaluates and injects necessary, system-generated fields: 
     - Generating a unique UUID for `transactionId`.
     - Recording a secure, immutable server-side timestamp (`timestamp = System.currentTimeMillis()`), avoiding reliance on potentially manipulated timestamps from the presentation layer.

3. **Event Transformation (Entity to Event Payload)**: 
   - The internal `Transaction` entity is subsequently transformed into a `TransactionEvent` DTO. This is fundamentally necessary to decouple microservices. The `TransactionEvent` strips away ORM/JPA-specific metadata and only transports the essential business data needed sequentially.
   - This transformed payload is serialized into byte streams over Apache Kafka asynchronously. When the `FraudEngineService` completes its calculations, it similarly transforms its internal DB entity (`FraudResult`) into a `FraudResultEvent` for the generic consumption of the Alerting service, abstracting the core complexities seamlessly.
