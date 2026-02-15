CREATE TABLE IF NOT EXISTS fraud_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL UNIQUE,
    user_id VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    device VARCHAR(50) NOT NULL,
    risk_score INT NOT NULL,
    decision VARCHAR(20) NOT NULL,
    reason TEXT,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_user_id (user_id),
    INDEX idx_decision (decision),
    INDEX idx_processed_at (processed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
