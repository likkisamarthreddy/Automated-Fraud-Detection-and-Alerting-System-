
The Level-1 Data Flow Diagram decomposes the Automated Fraud Detection and Alerting System into multiple internal processes.
The system first receives transaction data and validates it for correctness.
The validated transaction is analyzed using predefined fraud detection rules, and a risk score is calculated.
If the calculated risk exceeds a threshold, a fraud alert is triggered and sent to the Fraud Analyst and Notification Service.
All transaction details, fraud rules, and alert information are stored in their respective data stores for auditing and future analysis.
