package com.frauddetection.fraudengine.kafka;

import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.service.FraudEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final FraudEngineService fraudEngineService;

    public TransactionConsumer(FraudEngineService fraudEngineService) {
        this.fraudEngineService = fraudEngineService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            retryTopicSuffix = ".retry",
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "transactions.main", groupId = "fraud-group")
    public void consume(TransactionEvent event, Acknowledgment ack) {
        try {
            log.info("Received transaction event: txnId={}, userId={}, amount={}",
                    event.getTransactionId(), event.getUserId(), event.getAmount());

            fraudEngineService.processTransaction(event);

            ack.acknowledge();
            log.debug("Transaction acknowledged: txnId={}", event.getTransactionId());

        } catch (Exception e) {
            log.error("Error processing transaction: txnId={}, error={}",
                    event.getTransactionId(), e.getMessage(), e);
            throw e; // Let retry mechanism handle it
        }
    }
}
