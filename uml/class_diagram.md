# Q1. Identification of Key Classes

The key classes involved in the Automated Fraud Detection and Alerting System
are described below with their attributes, methods, and visibility.

## 1. Transaction
Represents a financial transaction received by the system.

Attributes:
- - transactionId : String
- - userId : String
- - amount : float
- - device : String
- - timestamp : datetime

Methods:
+ getTransactionId() : String  
+ getUserId() : String  
+ getAmount() : float  

## 2. RuleEngine
Applies rule-based fraud detection logic on transactions.

Attributes:
- - limit : float

Methods:
+ evaluate(transaction : Transaction) : boolean  

## 3. RiskAnalyzer
Calculates the risk score of a transaction.

Attributes:
- - baseScore : int

Methods:
+ calculate(transaction : Transaction) : int  

## 4. AlertService
Handles alert generation and notification.

Attributes:
- - channel : String

Methods:
+ sendAlert(userId : String, message : String) : void  

## 5. AuditLogger
Maintains logs for auditing and monitoring.

Attributes:
- - logs : List<String>

Methods:
+ logEvent(event : String) : void  

## 6. FraudDetectionSystem
Acts as the main controller coordinating all components.

Attributes:
- - ruleEngine : RuleEngine
- - riskAnalyzer : RiskAnalyzer
- - alertService : AlertService
- - auditLogger : AuditLogger

Methods:
+ processTransaction(transaction : Transaction) : void
