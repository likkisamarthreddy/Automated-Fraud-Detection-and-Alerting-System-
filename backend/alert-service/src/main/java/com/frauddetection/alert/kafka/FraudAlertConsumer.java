package com.frauddetection.alert.kafka;

import com.frauddetection.alert.service.AlertService;
import com.frauddetection.common.dto.FraudResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FraudAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(FraudAlertConsumer.class);

    private final AlertService alertService;

    public FraudAlertConsumer(AlertService alertService) {
        this.alertService = alertService;
    }

    @KafkaListener(topics = "fraud.alerts", groupId = "alert-group")
    public void consume(FraudResultEvent event) {
        try {
            log.info("Received fraud alert event: txnId={}, decision={}, risk={}",
                    event.getTransactionId(), event.getDecision(), event.getRiskScore());

            alertService.createAlert(event);

        } catch (Exception e) {
            log.error("Error processing fraud alert: txnId={}, error={}",
                    event.getTransactionId(), e.getMessage(), e);
        }
    }
}
