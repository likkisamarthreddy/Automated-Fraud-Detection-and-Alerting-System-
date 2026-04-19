CREATE DATABASE IF NOT EXISTS fraud_auth;
CREATE DATABASE IF NOT EXISTS fraud_transactions;
CREATE DATABASE IF NOT EXISTS fraud_engine;
CREATE DATABASE IF NOT EXISTS fraud_alerts;

GRANT ALL PRIVILEGES ON fraud_auth.* TO 'frauduser'@'%';
GRANT ALL PRIVILEGES ON fraud_transactions.* TO 'frauduser'@'%';
GRANT ALL PRIVILEGES ON fraud_engine.* TO 'frauduser'@'%';
GRANT ALL PRIVILEGES ON fraud_alerts.* TO 'frauduser'@'%';
FLUSH PRIVILEGES;
