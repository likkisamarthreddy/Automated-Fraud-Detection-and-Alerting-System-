# CS 331 (Software Engineering Lab) – Assignment 6  
## Automated Fraud Detection and Alerting System

---

# Part I. Choice of UI and Justification

## Chosen Interface  
**Direct Manipulation Interface (DMI) implemented as a Web-based Real-Time Dashboard**

## Justification

The **Automated Fraud Detection and Alerting System** analyzes large volumes of financial transactions in real time. Fraud analysts must continuously monitor transactions and respond quickly to suspicious activities. Therefore, the user interface must provide **fast visualization, easy interaction, and minimal user errors**.

A **Direct Manipulation Interface (DMI)** is the most appropriate UI type for this system.

---

### 1. Real-Time Visualization

Fraud analysts must continuously monitor incoming transactions and alerts.

The dashboard displays:

- Real-time fraud alerts  
- Transaction activity statistics  
- Risk score indicators  

Visual elements such as charts, alert cards, and color-coded indicators allow users to quickly understand the system status without reading complex logs.

---

### 2. Immediate User Interaction

Fraud detection systems require **quick decision making**.

The interface provides direct action options such as:

- Block Transaction  
- Allow Transaction  
- Mark for Review  

These actions can be performed directly from the dashboard, allowing analysts to respond immediately to suspicious transactions.

---

### 3. Efficient Data Exploration

Fraud patterns often appear when analyzing large numbers of transactions.

The dashboard allows users to:

- Filter transactions  
- Sort by fraud risk score  
- Search suspicious users  
- View transaction history  

These interactive features allow analysts to investigate fraud patterns efficiently.

---

### 4. Reduced Human Error

Command-line interfaces require remembering commands and syntax, which increases the chance of mistakes.

A graphical dashboard uses:

- Buttons  
- Dropdown menus  
- Form inputs  
- Toggle switches  

These elements reduce human errors and make the system easier to use.

---

### 5. Multi-Role Accessibility

Different users interact with the system, such as:

- Fraud Analysts  
- System Administrators  
- Developers  

The dashboard can provide different access levels and views based on user roles while maintaining a simple interface.

---

### Conclusion (Part I)

A **Direct Manipulation Dashboard Interface** is the most suitable UI design because it enables:

- real-time fraud monitoring  
- fast response to suspicious transactions  
- intuitive interaction with the system  
- reduced user errors  

---


# Part II. UI Implementation and User Interaction

The FraudGuard system provides a *web-based dashboard interface* where users interact with the fraud detection system.

The dashboard includes the following components:

- Fraud Alert Panel  
- Transaction Monitoring Table  
- Quick Action Buttons  
- Fraud Statistics Dashboard  

---

## 1. Fraud Alert Panel

The Fraud Alert Panel displays recent suspicious transactions detected by the fraud detection engine.

Features include:

- highlighting high-risk transactions  
- displaying transaction IDs and user details  
- showing alert timestamps  

This helps analysts quickly identify potential fraud cases.

---

## 2. Transaction Monitoring Table

The Transaction Monitoring Table displays all incoming transactions.

Typical columns include:

| Column | Description |
|------|-------------|
| Transaction ID | Unique transaction identifier |
| User ID | Account initiating the transaction |
| Transaction Amount | Value of the transaction |
| Risk Score | Fraud risk level assigned by system |
| Action Options | Controls for analyst actions |

Transactions with *high fraud risk scores* are visually highlighted.

---

## 3. Quick Action Options

Each suspicious transaction provides action options for the analyst.

### Block Transaction
Immediately stops the suspicious transaction.

### Allow Transaction
Marks the transaction as legitimate.

### Review Transaction
Sends the transaction for manual investigation.

These actions allow analysts to interact directly with the fraud detection system.
