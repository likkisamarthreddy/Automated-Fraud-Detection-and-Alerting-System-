package com.frauddetection.fraudengine.controller;

import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.service.FraudRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class FraudRuleController {

    private final FraudRuleService fraudRuleService;

    public FraudRuleController(FraudRuleService fraudRuleService) {
        this.fraudRuleService = fraudRuleService;
    }

    @GetMapping
    public ResponseEntity<List<FraudRule>> getAllRules() {
        return ResponseEntity.ok(fraudRuleService.getAllRules());
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<FraudRule>> getEnabledRules() {
        return ResponseEntity.ok(fraudRuleService.getEnabledRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FraudRule> getRuleById(@PathVariable Long id) {
        return fraudRuleService.getRuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FraudRule> createRule(@RequestBody FraudRule rule) {
        FraudRule created = fraudRuleService.createRule(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FraudRule> updateRule(@PathVariable Long id, @RequestBody FraudRule rule) {
        try {
            FraudRule updated = fraudRuleService.updateRule(id, rule);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        fraudRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
