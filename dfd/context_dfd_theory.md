
A Context Diagram or Level-0 Data Flow Diagram represents the entire system as
a single process. It shows how the system interacts with external entities
without describing internal processing or data storage. In this system,
transaction data is received from external sources and fraud alerts and
reports are generated for different users.

The primary external entities include the Transaction Source, Fraud Analyst, System Administrator, and Notification Service. The Transaction Source provides raw transaction information to the system, which is analyzed for potential fraudulent activity. When suspicious behavior is detected, the system generates fraud alerts that are delivered to the Fraud Analyst and sent through the Notification Service. Additionally, summarized reports and audit information are provided to the System Administrator for monitoring and management purposes. This high-level representation helps in clearly defining the system boundary, identifying external interactions, and understanding the overall flow of data between the system and its environment without exposing internal implementation details.
