UPDATE fraud_rules 
SET description = 'Flags transactions over ₹10,000' 
WHERE rule_name = 'Large Transaction Amount';
