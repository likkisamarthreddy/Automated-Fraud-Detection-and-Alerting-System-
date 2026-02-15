package com.frauddetection.fraudengine.service;

import com.frauddetection.common.dto.FraudResultEvent;
import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.engine.RuleEngine;
import com.frauddetection.fraudengine.entity.FraudResult;
import com.frauddetection.fraudengine.repository.FraudResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FraudEngineService {

    private static final Logger log = LoggerFactory.getLogger(FraudEngineService.class);
    private static final String FRAUD_ALERTS_TOPIC = "fraud.alerts";

    private final RuleEngine ruleEngine;
    private final IdempotencyService idempotencyService;
    private final FraudResultRepository fraudResultRepository;
    private final KafkaTemplate<String, FraudResultEvent> kafkaTemplate;

    @Value("${fraud.decision.block-threshold:80}")
    private int blockThreshold;

    @Value("${fraud.decision.alert-threshold:50}")
    private int alertThreshold;

    public FraudEngineService(RuleEngine ruleEngine,
            IdempotencyService idempotencyService,
            FraudResultRepository fraudResultRepository,
            KafkaTemplate<String, FraudResultEvent> kafkaTemplate,
            com.frauddetection.fraudengine.service.VelocityService velocityService) {
        this.ruleEngine = ruleEngine;
        this.idempotencyService = idempotencyService;
        this.fraudResultRepository = fraudResultRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.velocityService = velocityService;
    }

    private final com.frauddetection.fraudengine.service.VelocityService velocityService;

    @Transactional
    public void processTransaction(TransactionEvent event) {
        String txnId = event.getTransactionId();

        // Idempotency check
        if (idempotencyService.isAlreadyProcessed(txnId)) {
            return;
        }

        // Evaluate rules
        RuleEngine.RuleResult result = ruleEngine.evaluate(event);
        int riskScore = result.getRiskScore();
        String decision = result.getDecision(); // Use decision from RuleEngine

        // Persist result
        FraudResult fraudResult = new FraudResult(
                txnId,
                event.getUserId(),
                BigDecimal.valueOf(event.getAmount()),
                event.getDevice(),
                riskScore,
                decision,
                result.getReason());
        fraudResultRepository.save(fraudResult);

        // Update User Profile (Velocity, History, Devices)
        // Check if decision is BLOCK? If BLOCK, maybe consider it a "failure" for
        // future?
        // For now, we treat all as transaction attempts.
        try {
            velocityService.updateUserProfile(
                    event.getUserId(),
                    event.getAmount(),
                    event.getDevice(),
                    event.getTimestamp(),
                    "BLOCK".equals(decision) // Treat BLOCK as failure/suspicious to increment failure count?
                                             // User rule said "3+ failed attempts before success".
                                             // A BLOCK is a failed attempt at fraud.
            );
        } catch (Exception e) {
            log.error("Failed to update velocity profile", e);
        }

        // Mark as processed
        idempotencyService.markAsProcessed(txnId);

        log.info("Fraud analysis complete: txnId={}, risk={}, decision={}", txnId, riskScore, decision);

        // Always publish fraud result so Transaction Service can update the score
        FraudResultEvent alertEvent = new FraudResultEvent(
                txnId,
                event.getUserId(),
                event.getAmount(),
                event.getDevice(),
                riskScore,
                decision,
                result.getReason());
        kafkaTemplate.send(FRAUD_ALERTS_TOPIC, txnId, alertEvent);
        log.info("Fraud result published: txnId={}, score={}, decision={}", txnId, riskScore, decision);
    }
}
