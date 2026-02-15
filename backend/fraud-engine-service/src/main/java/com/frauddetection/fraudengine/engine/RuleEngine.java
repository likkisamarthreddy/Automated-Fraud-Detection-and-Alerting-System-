package com.frauddetection.fraudengine.engine;

import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.service.FraudRuleService;
import com.frauddetection.fraudengine.service.VelocityService;
import com.frauddetection.fraudengine.service.VelocityService.UserProfile;
import com.frauddetection.fraudengine.service.VelocityService.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleEngine {

    private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);

    private final VelocityService velocityService;
    private final FraudRuleService fraudRuleService;

    public RuleEngine(VelocityService velocityService, FraudRuleService fraudRuleService) {
        this.velocityService = velocityService;
        this.fraudRuleService = fraudRuleService;
    }

    public RuleResult evaluate(TransactionEvent event) {
        UserProfile profile = velocityService.getUserProfile(event.getUserId());
        List<TransactionRecord> history = velocityService.getRecentTransactions(event.getUserId());
        List<FraudRule> enabledRules = fraudRuleService.getEnabledRules();

        int score = 0;
        List<String> triggeredRules = new ArrayList<>();

        // Group rules by type
        List<FraudRule> amountRules = enabledRules.stream()
                .filter(r -> "AMOUNT".equalsIgnoreCase(r.getRuleType()))
                .collect(Collectors.toList());
        List<FraudRule> deviceRules = enabledRules.stream()
                .filter(r -> "DEVICE".equalsIgnoreCase(r.getRuleType()))
                .collect(Collectors.toList());
        List<FraudRule> velocityRules = enabledRules.stream()
                .filter(r -> "VELOCITY".equalsIgnoreCase(r.getRuleType()))
                .collect(Collectors.toList());

        // 1. AMOUNT RULES: check if transaction amount exceeds each rule's threshold
        for (FraudRule rule : amountRules) {
            if (event.getAmount() > rule.getThresholdValue()) {
                score += rule.getRiskScoreWeight();
                triggeredRules.add(rule.getRuleName() + " (>" + rule.getThresholdValue() + ")");
                log.debug("AMOUNT rule triggered: {} for amount={}", rule.getRuleName(), event.getAmount());
            }
        }

        // 2. VELOCITY RULES: count transactions in last 60 seconds
        long now = System.currentTimeMillis();
        long countIn60s = history.stream()
                .filter(t -> (now - t.timestamp) < 60000)
                .count() + 1; // +1 for current transaction

        for (FraudRule rule : velocityRules) {
            if (countIn60s > rule.getThresholdValue()) {
                score += rule.getRiskScoreWeight();
                triggeredRules.add(rule.getRuleName() + " (" + countIn60s + " txns)");
                log.debug("VELOCITY rule triggered: {} for count={}", rule.getRuleName(), countIn60s);
            }
        }

        // 3. DEVICE RULES: check if device is new/unknown
        boolean isNewDevice = !profile.getKnownDevices().contains(event.getDevice())
                && !profile.getKnownDevices().isEmpty();

        for (FraudRule rule : deviceRules) {
            if (isNewDevice) {
                score += rule.getRiskScoreWeight();
                triggeredRules.add(rule.getRuleName() + " (" + event.getDevice() + ")");
                log.debug("DEVICE rule triggered: {} for device={}", rule.getRuleName(), event.getDevice());
            }
        }

        // Cap risk score at 100
        score = Math.min(score, 100);

        // Decision based on score
        String decision;
        if (score >= 80)
            decision = "BLOCK";
        else if (score >= 50)
            decision = "ALERT";
        else
            decision = "ALLOW";

        String reason = triggeredRules.isEmpty()
                ? "All checks passed"
                : String.join(", ", triggeredRules);

        log.info("Rule evaluation: txnId={}, amount={}, score={}, decision={}, reason={}",
                event.getTransactionId(), event.getAmount(), score, decision, reason);

        return new RuleResult(score, decision, reason, triggeredRules);
    }

    public static class RuleResult {
        private int riskScore;
        private String decision;
        private String reason;
        private List<String> triggeredRules;

        public RuleResult(int riskScore, String decision, String reason, List<String> triggeredRules) {
            this.riskScore = riskScore;
            this.decision = decision;
            this.reason = reason;
            this.triggeredRules = triggeredRules;
        }

        public int getRiskScore() {
            return riskScore;
        }

        public String getDecision() {
            return decision;
        }

        public String getReason() {
            return reason;
        }

        public List<String> getTriggeredRules() {
            return triggeredRules;
        }
    }
}
