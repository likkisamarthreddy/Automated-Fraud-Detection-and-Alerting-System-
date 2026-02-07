# Q1. Identification of Key Classes

The Automated Fraud Detection and Alerting System is designed using
object-oriented principles. The key classes involved in the system are
identified below along with their attributes, functionalities (methods),
and visibility.

Visibility of members is explicitly mentioned as Public, Private, or
Protected to ensure proper encapsulation and controlled access.

---

## 1. Transaction
Represents a financial transaction received by the system.

### Attributes
- transactionId : String (Private)
- userId : String (Private)
- amount : float (Private)
- device : String (Private)
- timestamp : datetime (Private)

### Methods
- getTransactionId() : String (Public)
- getUserId() : String (Public)
- getAmount() : float (Public)

---

## 2. RuleEngine
Applies rule-based fraud detection logic on transactions.

### Attributes
- limit : float (Protected)

### Methods
- evaluate(transaction : Transaction) : boolean (Public)

---

## 3. RiskAnalyzer
Calculates the fraud risk score of a transaction.

### Attributes
- baseScore : int (Private)

### Methods
- calculate(transaction : Transaction) : int (Public)

---

## 4. AlertService
Handles fraud alert generation and notification.

### Attributes
- channel : String (Private)

### Methods
- sendAlert(userId : String, message : String) : void (Public)

---

## 5. AuditLogger
Maintains audit logs for monitoring and compliance purposes.

### Attributes
- logs : List<String> (Private)

### Methods
- logEvent(event : String) : void (Public)

---

## 6. FraudDetectionSystem
Acts as the main controller that coordinates all system components.

### Attributes
- ruleEngine : RuleEngine (Private)
- riskAnalyzer : RiskAnalyzer (Private)
- alertService : AlertService (Private)
- auditLogger : AuditLogger (Private)

### Methods
- processTransaction(transaction : Transaction) : void (Public)

---

## Conclusion
The above classes collectively define the structural foundation of the
Automated Fraud Detection and Alerting System. Proper use of access
modifiers ensures encapsulation, modularity, scalability, and
maintainability of the software system.
