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
