# Use Case Diagram – Automated Fraud Detection & Alerting System

## System Boundary

The **Automated Fraud Detection & Alerting System** is represented inside a system boundary.
All use cases such as fraud detection, decision making, and alert generation are performed within this boundary, while all actors remain outside the boundary.

## Actors
- **Customer**: Initiates transactions.
- **Payment Gateway**: Systems that forward transaction data.
- **Admin**: Manages rules and views reports.
- **Notification System (External)**: Sends emails/SMS.

## Use Case Relationships

```mermaid
usecaseDiagram
    actor "Customer" as User
    actor "Payment Gateway" as PG
    actor "Administrator" as Admin
    actor "Notification Service" as Notifier

    package "Fraud Detection System" {
        usecase "Submit Transaction" as UC1
        usecase "Validate Transaction" as UC2
        usecase "Analyze Fraud Risk" as UC3
        usecase "Generate Alert" as UC4
        usecase "Manage Fraud Rules" as UC5
        usecase "View Fraud Reports" as UC6
        usecase "Block Transaction" as UC7
    }

    User --> UC1
    PG --> UC1
    
    UC1 ..> UC2 : <<include>>
    UC2 ..> UC3 : <<include>>
    
    UC3 <.. UC4 : <<extend>>
    UC3 <.. UC7 : <<extend>>
    
    Admin --> UC5
    Admin --> UC6
    
    UC4 --> Notifier
```

### Explanation
1.  **Submit Transaction**: The core entry point where a Customer or Payment Gateway sends data.
2.  **Validate Transaction**: Ensures data integrity (part of submission).
3.  **Analyze Fraud Risk**: The internal process (Fraud Engine) that scores the transaction.
4.  **Generate Alert** / **Block Transaction**: Actions triggered *only* if the risk analysis deems the transaction suspicious (Extended behavior).
5.  **Manage Rules** / **View Reports**: Administrative functions for system configuration and monitoring.

