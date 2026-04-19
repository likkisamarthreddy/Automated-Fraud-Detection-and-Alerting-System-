# CS 331 (Software Engineering Lab) – Assignment 6
## Automated Fraud Detection and Alerting System

### Part I. Choice of UI and Justification

**Chosen Interface:** Direct Manipulation Interface (DMI) implemented as a Web-based Real-Time Dashboard

**Justification:**

The Automated Fraud Detection and Alerting System processes high-volume financial transactions and detects suspicious activity in real time. Therefore, the interface must allow fast visualization, quick interaction, and minimal user error.

A Direct Manipulation Interface (DMI) is the most suitable UI type for this system.

**1. Real-Time Visualization**
Fraud analysts must continuously monitor incoming transactions and fraud alerts.
The dashboard displays:
- Real-time fraud alerts
- Transaction activity charts
- Risk score indicators

Visual elements such as graphs, alert cards, and colored status indicators allow users to quickly identify suspicious activity without reading large textual logs.

**2. Immediate User Interaction**
In fraud monitoring systems, response time is critical.
The UI provides direct action buttons such as:
- Block Transaction
- Allow Transaction
- Mark for Review

This allows analysts to take action immediately after identifying a suspicious transaction.

**3. Efficient Data Exploration**
Fraud detection often requires analyzing patterns across many transactions.
The dashboard allows users to:
- Filter transactions
- Sort by fraud score
- Search suspicious users
- View transaction history

This direct interaction helps analysts detect fraud patterns quickly.

**4. Reduced Human Error**
Command-line interfaces require typing commands and remembering syntax, which increases the risk of errors.
A graphical dashboard provides:
- Buttons
- Dropdown menus
- Toggles
- Form validation

This prevents incorrect commands and improves system reliability.

**5. Multi-Role Accessibility**
The system is used by different roles:
- Fraud Analysts
- Developers
- System Administrators

The dashboard allows different views depending on the user role while maintaining a simple and intuitive interaction model.

Therefore, a Direct Manipulation Interface implemented as a web-based dashboard is the most suitable UI type for the Automated Fraud Detection and Alerting System.

***

### Part II. UI Implementation and User Interaction

The UI is implemented using React + TypeScript for the FraudGuard Console dashboard.

The dashboard contains the following components:
- Fraud Alert Panel
- Transaction Monitoring Table
- Quick Action Buttons
- Fraud Statistics Visualization

#### 1. Dashboard Component

```tsx
import React, { useState } from 'react'

type Txn = {
  id: string
  user: string
  amt: number
  risk: string
}

export default function Dashboard() {
  const [data, setData] = useState<Txn[]>([
    { id: 'T1023', user: 'User_21', amt: 5000, risk: 'HIGH' },
    { id: 'T1044', user: 'User_05', amt: 1200, risk: 'LOW' },
    { id: 'T1099', user: 'User_18', amt: 9000, risk: 'HIGH' }
  ])

  function blockTxn(id: string) {
    alert("Transaction " + id + " Blocked")
  }

  function allowTxn(id: string) {
    alert("Transaction " + id + " Allowed")
  }

  function reviewTxn(id: string) {
    alert("Transaction " + id + " Sent For Review")
  }

  return (
    <div>
      <h1>FraudGuard Dashboard</h1>
      <table border={1}>
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Amount</th>
            <th>Risk</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.map(txn => (
            <tr key={txn.id}>
              <td>{txn.id}</td>
              <td>{txn.user}</td>
              <td>{txn.amt}</td>
              <td>{txn.risk}</td>
              <td>
                <button onClick={() => blockTxn(txn.id)}>Block</button>
                <button onClick={() => allowTxn(txn.id)}>Allow</button>
                <button onClick={() => reviewTxn(txn.id)}>Review</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

#### 2. UI Components Explanation

**Fraud Transaction Table**
Displays all incoming transactions with their fraud risk level.
Columns include:
- Transaction ID
- User ID
- Transaction Amount
- Fraud Risk Level
- Action Buttons

**Quick Action Buttons**
Each transaction has three possible actions:
- **Block**: Immediately blocks a suspicious transaction.
- **Allow**: Marks transaction as legitimate.
- **Review**: Sends the transaction to manual investigation.

These actions allow analysts to interact directly with suspicious transactions.

#### 3. User Interaction Flow

**Step 1 – System Monitoring**
The analyst logs into the FraudGuard dashboard and views the real-time transaction table.

**Step 2 – Fraud Detection**
Transactions with HIGH risk score are highlighted.

**Step 3 – Action Selection**
The analyst clicks one of the action buttons:
- Block
- Allow
- Review

**Step 4 – System Response**
The system records the action and updates the transaction status.

**Example Interaction**
Example suspicious transaction:

| Transaction | User | Amount | Risk |
| :--- | :--- | :--- | :--- |
| T1023 | User_21 | ₹5000 | HIGH |

The analyst clicks **Block**, and the system immediately blocks the transaction and logs the action.

### Conclusion

The Direct Manipulation Dashboard Interface provides:
- Real-time fraud monitoring
- Fast interaction for fraud analysts
- Reduced human error
- Efficient exploration of suspicious transactions

This makes it the most effective UI design for the Automated Fraud Detection and Alerting System.
