package com.frauddetection.fraudengine.service;

import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.repository.FraudRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FraudRuleService {

    private static final Logger log = LoggerFactory.getLogger(FraudRuleService.class);

    private final FraudRuleRepository fraudRuleRepository;

    public FraudRuleService(FraudRuleRepository fraudRuleRepository) {
        this.fraudRuleRepository = fraudRuleRepository;
    }

    @Cacheable("fraudRules")
    public List<FraudRule> getAllRules() {
        return fraudRuleRepository.findAll();
    }

    @Cacheable("enabledFraudRules")
    public List<FraudRule> getEnabledRules() {
        return fraudRuleRepository.findByEnabledTrue();
    }

    public Optional<FraudRule> getRuleById(Long id) {
        return fraudRuleRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = { "fraudRules", "enabledFraudRules" }, allEntries = true)
    public FraudRule createRule(FraudRule rule) {
        log.info("Creating new fraud rule: {}", rule.getRuleName());
        return fraudRuleRepository.save(rule);
    }

    @Transactional
    @CacheEvict(value = { "fraudRules", "enabledFraudRules" }, allEntries = true)
    public FraudRule updateRule(Long id, FraudRule updatedRule) {
        return fraudRuleRepository.findById(id)
                .map(existingRule -> {
                    existingRule.setRuleName(updatedRule.getRuleName());
                    existingRule.setRuleType(updatedRule.getRuleType());
                    existingRule.setEnabled(updatedRule.getEnabled());
                    existingRule.setThresholdValue(updatedRule.getThresholdValue());
                    existingRule.setRiskScoreWeight(updatedRule.getRiskScoreWeight());
                    existingRule.setDescription(updatedRule.getDescription());
                    log.info("Updated fraud rule: {} (ID: {})", existingRule.getRuleName(), id);
                    return fraudRuleRepository.save(existingRule);
                })
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = { "fraudRules", "enabledFraudRules" }, allEntries = true)
    public void deleteRule(Long id) {
        log.info("Deleting fraud rule with ID: {}", id);
        fraudRuleRepository.deleteById(id);
    }
}
