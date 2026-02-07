# Q1. Identification of Key Classes

The Automated Fraud Detection and Alerting System is designed using
object-oriented principles. The key classes involved in the system are
identified below along with their attributes, functionalities (methods),
and visibility.

## Visibility Notation (UML)
- + : Public
- - : Private
- # : Protected

---

## 1. Transaction
Represents a financial transaction received by the system.

### Attributes
- - transactionId : String
- - userId : String
- - amount : float
- - device : String
- - timestamp : datetime

### Methods
- + getTransactionId() : String
- + getUserId() : String
- + getAmount() : float

---

## 2. RuleEngine
Applies rule-based fraud detection logic on transactions.

### Attributes
- - limit : float

### Methods
- + evaluate(transaction : Transaction) : boolean

---

## 3. RiskAnalyzer
Calculates the fraud risk score of a transaction.

### Attributes
- - baseScore : int

### Methods
- + calculate(transaction : Transaction) : int

---

## 4. AlertService
Handles fraud alert generation and notification.

### Attributes
- - channel : String

### Methods
- + sendAlert(userId : String, message : String) : void

---

## 5. AuditLogger
Maintains audit logs for monitoring and compliance purposes.

### Attributes
- - logs : List<String>

### Methods
- + logEvent(event : String) : void

---

## 6. FraudDetectionSystem
Acts as the main controller that coordinates all system components.

### Attributes
- - ruleEngine : RuleEngine
- - riskAnalyzer : RiskAnalyzer
- - alertService : AlertService
- - auditLogger : AuditLogger

### Methods
- + processTransaction(transaction : Transaction) : void

---

## Conclusion
These classes collectively define the structural foundation of the
Automated Fraud Detection and Alerting System. Each class has a clearly
defined responsibility, supporting modularity, scalability, and
maintainability of the system.
