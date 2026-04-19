package com.frauddetection.fraudengine.engine;

import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.service.FraudRuleService;
import com.frauddetection.fraudengine.service.VelocityService;
import com.frauddetection.fraudengine.service.VelocityService.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class RuleEngineTest {

    @Mock
    private VelocityService velocityService;

    @Mock
    private FraudRuleService fraudRuleService;

    private RuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleEngine = new RuleEngine(velocityService, fraudRuleService);
    }

    @Test
    void testEvaluateAmountRule_ThresholdExceeded() {
        // Prepare mocks
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId("tx1");
        event.setUserId("user1");
        event.setAmount(1500.0);
        event.setDevice("device1");

        UserProfile profile = new UserProfile();
        profile.setKnownDevices(new HashSet<>(Collections.singletonList("device1")));
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        FraudRule rule = new FraudRule();
        rule.setRuleName("High Amount");
        rule.setRuleType("AMOUNT");
        rule.setThresholdValue(1000);
        rule.setRiskScoreWeight(60);
        rule.setEnabled(true);

        when(fraudRuleService.getEnabledRules()).thenReturn(Collections.singletonList(rule));

        // Evaluate
        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        // Verify (White Box: checking internal logic calculation)
        assertEquals(60, result.getRiskScore());
        assertEquals("ALERT", result.getDecision());
        assertEquals("High Amount (>1000)", result.getReason());
    }

    @Test
    void testEvaluateAmountRule_BelowThreshold() {
        // Prepare mocks
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId("tx2");
        event.setUserId("user2");
        event.setAmount(500.0);
        event.setDevice("device2");

        UserProfile profile = new UserProfile();
        profile.setKnownDevices(new HashSet<>(Collections.singletonList("device2")));
        when(velocityService.getUserProfile("user2")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user2")).thenReturn(new ArrayList<>());

        FraudRule rule = new FraudRule();
        rule.setRuleName("High Amount");
        rule.setRuleType("AMOUNT");
        rule.setThresholdValue(1000);
        rule.setRiskScoreWeight(60);
        rule.setEnabled(true);

        when(fraudRuleService.getEnabledRules()).thenReturn(Collections.singletonList(rule));

        // Evaluate
        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        // Verify
        assertEquals(0, result.getRiskScore());
        assertEquals("ALLOW", result.getDecision());
        assertEquals("All checks passed", result.getReason());
    }

    @Test
    void testEvaluateDeviceRule_NewDevice() {
        // Prepare mocks
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId("tx3");
        event.setUserId("user3");
        event.setAmount(100.0);
        event.setDevice("new_device");

        // Profile has old_device, so new_device is "new"
        UserProfile profile = new UserProfile();
        profile.setKnownDevices(new HashSet<>(Collections.singletonList("old_device")));
        when(velocityService.getUserProfile("user3")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user3")).thenReturn(new ArrayList<>());

        FraudRule rule = new FraudRule();
        rule.setRuleName("New Device");
        rule.setRuleType("DEVICE");
        rule.setRiskScoreWeight(50);
        rule.setEnabled(true);

        when(fraudRuleService.getEnabledRules()).thenReturn(Collections.singletonList(rule));

        // Evaluate
        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        // Verify
        assertEquals(50, result.getRiskScore());
        assertEquals("ALERT", result.getDecision());
        assertEquals("New Device (new_device)", result.getReason());
    }
}
