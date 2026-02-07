# UML Class Diagram – Automated Fraud Detection and Alerting System

## Transaction
- -transactionId : String
- -userId : String
- -amount : float
- -device : String
- -timestamp : datetime

+getTransactionId()
+getUserId()
+getAmount()

## RuleEngine
- -limit : float
+evaluate(transaction)

## RiskAnalyzer
- -baseScore : int
+calculate(transaction)

## AlertService
- -channel : String
+sendAlert(userId,message)

## AuditLogger
- -logs : list
+logEvent(event)

## FraudDetectionSystem
- -ruleEngine : RuleEngine
- -riskAnalyzer : RiskAnalyzer
- -alertService : AlertService
- -auditLogger : AuditLogger

+processTransaction(transaction)
