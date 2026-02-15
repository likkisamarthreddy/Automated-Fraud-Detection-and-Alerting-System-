# Sequence Diagram - Suspicious Transaction Flow

This sequence diagram depicts the flow of operations when a user initiates a transaction that is eventually flagged as fraudulent.

## Participants
- **User**: The initiator.
- **API Gateway**: Entry point.
- **Transaction Service**: Manages transaction lifecycle.
- **Fraud Engine**: Analyzes risk.
- **Alert Service**: Notifies stakeholders.
- **Database**: Persistence layer.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant GW as API Gateway
    participant TS as Transaction Service
    participant FE as Fraud Engine
    participant DB as Database
    participant AS as Alert Service

    User->>GW: POST /transaction (Amount: $15,000)
    GW->>TS: Forward Request
    
    TS->>TS: Validate Request
    TS->>DB: Save Transaction (Status: PENDING)
    TS-->>GW: 202 Accepted
    GW-->>User: Transaction Processing
    
    par Async Analysis
        TS->>FE: AnalyzeTransaction(TxID)
    end
    
    FE->>DB: Fetch User History & Rules
    FE->>FE: Execute Rules
    FE->>FE: Calculate Risk Score (Score: 85/100)
    
    alt Score > Threshold
        FE->>AS: TriggerAlert(TxID, HighRisk)
        AS->>User: Send SMS ("Suspicious Activity Detected")
        AS->>DB: Log Alert
        FE->>TS: UpdateStatus(BLOCKED)
    else Score < Threshold
        FE->>TS: UpdateStatus(APPROVED)
    end
```
