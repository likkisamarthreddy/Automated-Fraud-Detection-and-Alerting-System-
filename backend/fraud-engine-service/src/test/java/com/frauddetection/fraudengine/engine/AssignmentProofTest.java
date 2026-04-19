package com.frauddetection.fraudengine.engine;

import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.service.FraudRuleService;
import com.frauddetection.fraudengine.service.VelocityService;
import com.frauddetection.fraudengine.service.VelocityService.UserProfile;
import com.frauddetection.fraudengine.service.VelocityService.TransactionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AssignmentProofTest {

    @Mock
    private VelocityService velocityService;

    @Mock
    private FraudRuleService fraudRuleService;

    @InjectMocks
    private RuleEngine ruleEngine;

    private List<FraudRule> defaultRules;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default velocity window (10 mins)
        ReflectionTestUtils.setField(ruleEngine, "velocityWindowMs", 600000L);

        // Define standard rules from migration V2
        FraudRule r1 = new FraudRule();
        r1.setRuleName("Large Transaction Amount");
        r1.setRuleType("AMOUNT");
        r1.setThresholdValue(10000);
        r1.setRiskScoreWeight(50);
        r1.setEnabled(true);

        FraudRule r2 = new FraudRule();
        r2.setRuleName("Suspicious Device");
        r2.setRuleType("DEVICE");
        r2.setRiskScoreWeight(40);
        r2.setEnabled(true);

        FraudRule r3 = new FraudRule();
        r3.setRuleName("High Velocity");
        r3.setRuleType("VELOCITY");
        r3.setThresholdValue(5);
        r3.setRiskScoreWeight(60);
        r3.setEnabled(true);

        defaultRules = Arrays.asList(r1, r2, r3);
        when(fraudRuleService.getEnabledRules()).thenReturn(defaultRules);
    }

    @Test
    void TC_FE_001_NormalTransaction() {
        TransactionEvent event = createEvent("user1", 500.0, "known_device");
        UserProfile profile = createProfile("known_device");
        
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-001] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(0, result.getRiskScore());
        assertEquals("ALLOW", result.getDecision());
    }

    @Test
    void TC_FE_002_LargeTransaction() {
        TransactionEvent event = createEvent("user1", 15000.0, "known_device");
        UserProfile profile = createProfile("known_device");
        
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-002] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(50, result.getRiskScore());
        assertEquals("ALERT", result.getDecision());
    }

    @Test
    void TC_FE_003_NewDevice() {
        TransactionEvent event = createEvent("user1", 100.0, "new_device");
        UserProfile profile = createProfile("old_device"); // Existing history
        
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-003] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(40, result.getRiskScore());
        assertEquals("ALLOW", result.getDecision());
    }

    @Test
    void TC_FE_004_CriticalRisk() {
        TransactionEvent event = createEvent("user1", 12000.0, "unknown_x");
        UserProfile profile = createProfile("old_device");
        
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-004] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(90, result.getRiskScore()); // 50 + 40
        assertEquals("BLOCK", result.getDecision());
    }

    @Test
    void TC_FE_005_HighVelocity() {
        TransactionEvent event = createEvent("user1", 100.0, "known_device");
        UserProfile profile = createProfile("known_device");
        
        // 5 existing txns in last 2 mins (total 6 including current)
        List<TransactionRecord> history = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            history.add(new TransactionRecord(50.0, now - 120000, "known_device"));
        }

        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(history);

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-005] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(60, result.getRiskScore());
        assertEquals("ALERT", result.getDecision());
    }

    @Test
    void TC_FE_006_MaximumScoreCap() {
        TransactionEvent event = createEvent("user1", 20000.0, "unknown_x");
        UserProfile profile = createProfile("old_device");
        
        // High velocity (6th txn)
        List<TransactionRecord> history = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            history.add(new TransactionRecord(50.0, now - 120000, "unknown_x"));
        }

        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(history);

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-006] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(100, result.getRiskScore()); // 50 + 40 + 60 = 150 -> capped at 100
        assertEquals("BLOCK", result.getDecision());
    }

    @Test
    void TC_FE_007_BoundaryValue() {
        TransactionEvent event = createEvent("user1", 10000.0, "known_device");
        UserProfile profile = createProfile("known_device");
        
        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-007] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(0, result.getRiskScore());
        assertEquals("ALLOW", result.getDecision());
    }

    @Test
    void TC_FE_008_RuleDisabled() {
        TransactionEvent event = createEvent("user1", 15000.0, "known_device");
        UserProfile profile = createProfile("known_device");
        
        // Amount rule disabled
        when(fraudRuleService.getEnabledRules()).thenReturn(Arrays.asList(defaultRules.get(1), defaultRules.get(2)));

        when(velocityService.getUserProfile("user1")).thenReturn(profile);
        when(velocityService.getRecentTransactions("user1")).thenReturn(new ArrayList<>());

        RuleEngine.RuleResult result = ruleEngine.evaluate(event);

        System.out.println("[TC-FE-008] Score: " + result.getRiskScore() + ", Decision: " + result.getDecision());
        assertEquals(0, result.getRiskScore());
        assertEquals("ALLOW", result.getDecision());
    }

    private TransactionEvent createEvent(String userId, double amount, String device) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(UUID.randomUUID().toString());
        event.setUserId(userId);
        event.setAmount(amount);
        event.setDevice(device);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }

    private UserProfile createProfile(String... devices) {
        UserProfile profile = new UserProfile();
        profile.setKnownDevices(new HashSet<>(Arrays.asList(devices)));
        return profile;
    }
}
