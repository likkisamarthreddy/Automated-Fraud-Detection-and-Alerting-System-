# Q2. UML Class Diagram and Relationships

The UML class diagram represents the static structure of the system and
relationships among classes.

## Relationships Used

Relationship Type | Classes Involved | Description | Cardinality
--- | --- | --- | ---
Association | FraudDetectionSystem → Transaction | Processes transactions | 1 → 0..*
Composition | FraudDetectionSystem ◆ RuleEngine | Core engine owned by system | 1 → 1
Association | FraudDetectionSystem → RiskAnalyzer | Uses risk analysis | 1 → 1
Association | FraudDetectionSystem → AlertService | Sends alerts | 1 → 1
Association | FraudDetectionSystem → AuditLogger | Logs events | 1 → 1

## Cardinality Explanation

- One FraudDetectionSystem can process multiple transactions.
- Each system instance contains exactly one RuleEngine.
- Alerting and logging services are associated on a one-to-one basis.
