# Use Case Diagram – Automated Fraud Detection & Alerting System

## System Boundary

The **Automated Fraud Detection & Alerting System** is represented inside a system boundary.  
All use cases such as fraud detection, decision making, and alert generation are performed within this boundary, while all actors remain outside the boundary.



## Use Case Relationships

### 1. Association Relationship

- **Customer** → Submit Transaction  
- **Payment Gateway** → Submit Transaction  
- **Admin** → View Fraud Reports  
- **Admin** → Manage Fraud Rules  
- **Notification Service** → Notify User  



### 2. Include Relationship

- **Submit Transaction** includes **Validate Transaction Data**  
- **Make Fraud Decision** includes **Apply Fraud Rules**  
- **Make Fraud Decision** includes **Perform ML Fraud Scoring**  



### 3. Extend Relationship

- **Block Transaction** extends **Make Fraud Decision**  
- **Generate Alert** extends **Make Fraud Decision**  

> These extended actions occur only when suspicious or fraudulent activity is detected.



## Explanation of the Use Case Diagram

In the Automated Fraud Detection & Alerting System, the **Customer** or **Payment Gateway** submits transaction details to the system. The system first validates the transaction data, then applies predefined fraud detection rules and performs machine learning–based fraud scoring. Based on this analysis, a fraud decision is made.

If fraudulent activity is detected, the transaction may be blocked and alerts are generated. The **Admin** can manage fraud detection rules and view fraud reports, while notifications are sent to users or administrators through the **Notification Service**.

---

## Conclusion

The Use Case Diagram clearly represents the **behavioral aspects** of the Automated Fraud Detection & Alerting System. It identifies the actors, system functionalities, and relationships among use cases. This diagram helps in understanding the system scope, functional requirements, and user interactions, making it an essential part of software design.
