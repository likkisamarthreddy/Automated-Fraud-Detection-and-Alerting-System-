package com.frauddetection.transaction.kafka;

import com.frauddetection.common.dto.FraudResultEvent;
import com.frauddetection.transaction.entity.Transaction;
import com.frauddetection.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FraudResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(FraudResultConsumer.class);

    private final TransactionRepository transactionRepository;

    public FraudResultConsumer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "${kafka.topic.fraud-result}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(FraudResultEvent event) {
        try {
            log.info("Received Fraud Result for txnId={}: score={}, decision={}",
                    event.getTransactionId(), event.getRiskScore(), event.getDecision());

            Transaction transaction = transactionRepository.findById(event.getTransactionId())
                    .orElse(null);

            if (transaction != null) {
                transaction.setRiskScore(event.getRiskScore());
                transaction.setDecision(event.getDecision());
                transaction.setReason(event.getReason());
                transactionRepository.save(transaction);
                log.info("Updated transaction {} with fraud result: risk={}, decision={}",
                        event.getTransactionId(), event.getRiskScore(), event.getDecision());
            } else {
                log.warn("Transaction not found for id: {}", event.getTransactionId());
            }

        } catch (Exception e) {
            log.error("Error processing fraud result event", e);
        }
    }
}
