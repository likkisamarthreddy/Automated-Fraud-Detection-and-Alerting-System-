package com.frauddetection.alert.service;

import com.frauddetection.alert.entity.Alert;
import com.frauddetection.alert.repository.AlertRepository;
import com.frauddetection.common.dto.FraudResultEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * WHITE BOX TEST: Tests the internal logic of AlertService.
 * We verify alert creation, throttling logic, and status updates.
 */
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alertService = new AlertService(alertRepository);
        // Set throttle properties via reflection (normally injected by Spring)
        ReflectionTestUtils.setField(alertService, "throttleWindowSeconds", 300L);
        ReflectionTestUtils.setField(alertService, "maxAlertsPerUser", 5L);
    }

    /**
     * White Box Test 1: Verify that an alert is created and saved to the DAL
     * with correct fields mapped from the FraudResultEvent.
     */
    @Test
    void testCreateAlert_Success() {
        FraudResultEvent event = new FraudResultEvent(
                "tx-alert-1", "user10", 12000.0, "phone", 90, "BLOCK", "High Amount");

        // No recent alerts (below throttle limit)
        when(alertRepository.countByUserIdAndCreatedAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert created = alertService.createAlert(event);

        assertNotNull(created);
        assertEquals("tx-alert-1", created.getTransactionId());
        assertEquals("user10", created.getUserId());
        assertEquals(BigDecimal.valueOf(12000.0), created.getAmount());
        assertEquals(90, created.getRiskScore());
        assertEquals("BLOCK", created.getDecision());
        assertEquals("OPEN", created.getStatus());

        verify(alertRepository).save(any(Alert.class));
    }

    /**
     * White Box Test 2: Verify the throttling logic — when a user already has
     * maxAlertsPerUser (5) recent alerts, new alerts are suppressed (returns null).
     * Tests the internal branch: if (recentAlerts >= maxAlertsPerUser) return null;
     */
    @Test
    void testCreateAlert_ThrottledWhenMaxExceeded() {
        FraudResultEvent event = new FraudResultEvent(
                "tx-throttled", "spammer", 500.0, "device", 55, "ALERT", "Velocity");

        // Simulate 5 recent alerts (at the throttle limit)
        when(alertRepository.countByUserIdAndCreatedAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(5L);

        Alert result = alertService.createAlert(event);

        assertNull(result, "Alert should be null when throttled");
        verify(alertRepository, never()).save(any());
    }

    /**
     * White Box Test 3: Verify that updateAlertStatus() correctly updates the status
     * and sets the resolvedAt timestamp via the DAL.
     */
    @Test
    void testUpdateAlertStatus_SetsStatusAndResolvedAt() {
        Alert existingAlert = new Alert("tx-update", "user20", BigDecimal.valueOf(3000), 65, "ALERT", "Device");
        when(alertRepository.findById(1L)).thenReturn(Optional.of(existingAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert updated = alertService.updateAlertStatus(1L, "BLOCKED");

        assertEquals("BLOCKED", updated.getStatus());
        assertNotNull(updated.getResolvedAt(), "resolvedAt should be set on status update");
        verify(alertRepository).save(existingAlert);
    }
}
