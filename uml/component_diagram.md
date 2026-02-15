# Component Diagram - System Architecture

This Component Diagram provides a high-level view of the software components (Microservices) and their dependencies, including databases and external systems.

## Components
*   **API Gateway**: Routes traffic.
*   **Auth Service**: Provides JWT tokens.
*   **Transaction Service**: Core business logic.
*   **Fraud Engine**: Verification logic.
*   **Alert Service**: Notification logic.
*   **Databases**: Dedicated data stores for services (Pattern: Database-per-service).

```mermaid
componentDiagram
    package "Client Layer" {
        [Web App / Mobile] as Client
    }

    package "API Layer" {
        [API Gateway] as GW
    }

    package "Microservices" {
        [Auth Service] as Auth
        [Transaction Service] as Tx
        [Fraud Engine] as Fraud
        [Alert Service] as Alert
    }

    package "Data Layer" {
        database "Auth DB" as DB1
        database "Tx DB" as DB2
        database "Risk DB" as DB3
    }
    
    Client --> GW : HTTPS/REST
    GW --> Auth : Validate Token
    GW --> Tx : Submit Tx
    
    Tx --> Fraud : Async analysis via Kafka/bRPC
    Fraud --> Alert : High Risk Event
    
    Auth ..> DB1
    Tx ..> DB2
    Fraud ..> DB3
```
