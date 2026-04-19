package com.frauddetection.alert.service;

import com.frauddetection.alert.entity.Alert;
import com.frauddetection.alert.repository.AlertRepository;
import com.frauddetection.common.dto.FraudResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;

    @Value("${alert.throttle.window-seconds:300}")
    private long throttleWindowSeconds;

    @Value("${alert.throttle.max-alerts-per-user:5}")
    private long maxAlertsPerUser;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public Alert createAlert(FraudResultEvent event) {
        // Throttle check
        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(throttleWindowSeconds);
        long recentAlerts = alertRepository.countByUserIdAndCreatedAtAfter(event.getUserId(), windowStart);

        if (recentAlerts >= maxAlertsPerUser) {
            log.warn("Alert throttled: userId={}, recentAlerts={}, max={}",
                    event.getUserId(), recentAlerts, maxAlertsPerUser);
            return null;
        }

        Alert alert = new Alert(
                event.getTransactionId(),
                event.getUserId(),
                BigDecimal.valueOf(event.getAmount()),
                event.getRiskScore(),
                event.getDecision(),
                event.getReason()
        );

        alertRepository.save(alert);
        log.info("Alert created: alertId={}, txnId={}, decision={}",
                alert.getAlertId(), event.getTransactionId(), event.getDecision());

        return alert;
    }

    public Page<Alert> getAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }

    public Page<Alert> getAlertsByStatus(String status, Pageable pageable) {
        return alertRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Alert updateAlertStatus(Long alertId, String status) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        alert.setStatus(status);
        alert.setResolvedAt(LocalDateTime.now());
        alertRepository.save(alert);

        log.info("Alert status updated: alertId={}, status={}", alertId, status);
        return alert;
    }

    public Map<String, Object> getAlertMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAlerts", alertRepository.count());
        metrics.put("openAlerts", alertRepository.countByStatus("OPEN"));
        
        long total = alertRepository.count();
        long open = alertRepository.countByStatus("OPEN");
        metrics.put("resolvedAlerts", total - open);
        return metrics;
    }
}
