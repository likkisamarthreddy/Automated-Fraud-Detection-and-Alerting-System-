This diagram illustrates how transaction data moves through different internal processes of the system.
After receiving and validating the transaction, the system analyzes it using fraud detection logic and computes a risk score.
Based on the risk score, the system may generate a fraud alert, which is communicated to relevant stakeholders and stored in audit logs.
The presence of data stores ensures persistence of transaction history, fraud rules, and generated alerts.
