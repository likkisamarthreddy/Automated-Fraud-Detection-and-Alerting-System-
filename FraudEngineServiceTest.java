package com.frauddetection.fraudengine.service;

import com.frauddetection.common.dto.FraudResultEvent;
import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.engine.RuleEngine;
import com.frauddetection.fraudengine.entity.FraudResult;
import com.frauddetection.fraudengine.repository.FraudResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * WHITE BOX TEST: Tests the internal logic of FraudEngineService.processTransaction().
 * We verify idempotency checks, result persistence to DAL, and Kafka event publishing.
 */
class FraudEngineServiceTest {

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private FraudResultRepository fraudResultRepository;

    @Mock
    private KafkaTemplate<String, FraudResultEvent> kafkaTemplate;

    @Mock
    private VelocityService velocityService;

    private FraudEngineService fraudEngineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fraudEngineService = new FraudEngineService(
                ruleEngine, idempotencyService, fraudResultRepository, kafkaTemplate, velocityService);
    }

    /**
     * White Box Test 1: Verify that duplicate transactions are skipped via idempotency check.
     * Tests the internal branch: if (idempotencyService.isAlreadyProcessed(txnId)) return;
     */
    @Test
    void testProcessTransaction_DuplicateIsSkipped() {
        TransactionEvent event = createEvent("dup-tx-1", "user1", 500.0, "phone");

        // Simulate already-processed transaction
        when(idempotencyService.isAlreadyProcessed("dup-tx-1")).thenReturn(true);

        fraudEngineService.processTransaction(event);

        // Verify: rule engine should NEVER be called for duplicates
        verify(ruleEngine, never()).evaluate(any());
        verify(fraudResultRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    /**
     * White Box Test 2: Verify that fraud results are persisted to the DAL (database)
     * with the correct fields from the rule engine output.
     */
    @Test
    void testProcessTransaction_ResultPersistedToDatabase() {
        TransactionEvent event = createEvent("tx-100", "user5", 15000.0, "laptop");

        when(idempotencyService.isAlreadyProcessed("tx-100")).thenReturn(false);
        when(ruleEngine.evaluate(event)).thenReturn(
                new RuleEngine.RuleResult(85, "BLOCK", "High Amount (>10000)",
                        Collections.singletonList("High Amount (>10000)")));

        fraudEngineService.processTransaction(event);

        // Capture the FraudResult saved to the repository (DAL layer)
        ArgumentCaptor<FraudResult> captor = ArgumentCaptor.forClass(FraudResult.class);
        verify(fraudResultRepository).save(captor.capture());

        FraudResult saved = captor.getValue();
        assertEquals("tx-100", saved.getTransactionId());
        assertEquals("user5", saved.getUserId());
        assertEquals(BigDecimal.valueOf(15000.0), saved.getAmount());
        assertEquals(85, saved.getRiskScore());
        assertEquals("BLOCK", saved.getDecision());
    }

    /**
     * White Box Test 3: Verify that a Kafka event is published after processing,
     * ensuring the fraud result reaches the alert-service downstream.
     */
    @Test
    void testProcessTransaction_KafkaEventPublished() {
        TransactionEvent event = createEvent("tx-200", "user8", 7500.0, "tablet");

        when(idempotencyService.isAlreadyProcessed("tx-200")).thenReturn(false);
        when(ruleEngine.evaluate(event)).thenReturn(
                new RuleEngine.RuleResult(60, "ALERT", "High Amount (>5000)",
                        Collections.singletonList("High Amount (>5000)")));

        fraudEngineService.processTransaction(event);

        // Verify Kafka publish with correct topic and key
        ArgumentCaptor<FraudResultEvent> eventCaptor = ArgumentCaptor.forClass(FraudResultEvent.class);
        verify(kafkaTemplate).send(eq("fraud.alerts"), eq("tx-200"), eventCaptor.capture());

        FraudResultEvent published = eventCaptor.getValue();
        assertEquals("tx-200", published.getTransactionId());
        assertEquals(60, published.getRiskScore());
        assertEquals("ALERT", published.getDecision());
    }

    private TransactionEvent createEvent(String txnId, String userId, double amount, String device) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(txnId);
        event.setUserId(userId);
        event.setAmount(amount);
        event.setDevice(device);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }
}
