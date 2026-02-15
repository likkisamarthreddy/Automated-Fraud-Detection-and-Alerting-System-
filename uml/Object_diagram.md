# UML Object Diagram (Runtime View)

The object diagram represents runtime instances of the system's classes during the processing of a specific **Suspicious Transaction**.

**Scenario**: A user submits a high-value transaction ($15,000) which triggers a definition of fraud.

## Runtime Objects

*   **req1**: An instance of the incoming HTTP request.
*   **txService**: The active instance of the Transaction Service logic.
*   **tx123**: The specific Transaction entity created in memory.
*   **fraudEngine**: The service instance performing analysis.
*   **rule_HighValue**: A specific rule policy object loaded for evaluation.

```mermaid
graph TD
    req1[request: HttpServletRequest] -- "POST /api/transactions" --> ctrl[txController: TransactionController]
    ctrl -- calls --> svc[txService: TransactionService]
    svc -- creates --> tx1[tx123: Transaction]
    svc -- "async call" --> f_svc[fraudService: FraudAnalysisService]
    
    f_svc -- evaluates --> rule1[rule_HighValue: FraudRule]
    rule1 -- returns --> viol[violation: RuleResult]
    
    f_svc -- generates --> alert[alert99: FraudAlert]
    
    style req1 fill:#f9f,stroke:#333
    style tx1 fill:#ff9,stroke:#333
    style alert fill:#f99,stroke:#333
```

