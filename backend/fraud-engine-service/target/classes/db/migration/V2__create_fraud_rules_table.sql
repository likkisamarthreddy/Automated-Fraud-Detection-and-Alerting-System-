-- Configurable Fraud Rules Table
CREATE TABLE fraud_rules (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    threshold_value INT NOT NULL,
    risk_score_weight INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_type (rule_type),
    INDEX idx_enabled (enabled)
);

-- Insert default rules (migrating from hardcoded values)
INSERT INTO fraud_rules (rule_name, rule_type, enabled, threshold_value, risk_score_weight, description) VALUES
('Large Transaction Amount', 'AMOUNT', TRUE, 10000, 50, 'Flags transactions over $10,000'),
('Suspicious Device', 'DEVICE', TRUE, 1, 40, 'Flags transactions from suspicious devices'),
('High Velocity', 'VELOCITY', TRUE, 5, 60, 'Flags users with more than 5 transactions in 10 minutes');
