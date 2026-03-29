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
