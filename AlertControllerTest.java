package com.frauddetection.alert.rest;

import com.frauddetection.alert.controller.AlertController;
import com.frauddetection.alert.entity.Alert;
import com.frauddetection.alert.service.AlertService;
import com.frauddetection.common.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BLACK BOX TEST: Tests the Alert REST API endpoints as a "black box".
 * We only verify HTTP status codes and JSON response structure without
 * knowing the internal service or DAL implementation.
 */
@WebMvcTest(AlertController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    /**
     * Black Box Test 1: GET /api/alerts should return 200 OK
     * with a paginated list wrapped in ApiResponse.
     */
    @Test
    void testGetAlerts_ReturnsPagedList() throws Exception {
        Alert alert = new Alert("tx-bb-1", "user1", BigDecimal.valueOf(5000), 75, "ALERT", "High Amount");
        Page<Alert> page = new PageImpl<>(Collections.singletonList(alert));

        when(alertService.getAlerts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].transactionId").value("tx-bb-1"))
                .andExpect(jsonPath("$.data.content[0].riskScore").value(75));
    }

    /**
     * Black Box Test 2: PUT /api/alerts/{id}/block should return 200 OK
     * with the updated alert status set to "BLOCKED".
     */
    @Test
    void testBlockAlert_ReturnsUpdatedAlert() throws Exception {
        Alert blocked = new Alert("tx-bb-2", "user2", BigDecimal.valueOf(8000), 90, "BLOCK", "Amount + Device");
        blocked.setStatus("BLOCKED");

        when(alertService.updateAlertStatus(1L, "BLOCKED")).thenReturn(blocked);

        mockMvc.perform(put("/api/alerts/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("BLOCKED"));
    }

    /**
     * Black Box Test 3: GET /api/alerts/metrics should return 200 OK
     * with a JSON object containing totalAlerts, openAlerts, and resolvedAlerts.
     */
    @Test
    void testGetMetrics_ReturnsMetricsJson() throws Exception {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAlerts", 42L);
        metrics.put("openAlerts", 10L);
        metrics.put("resolvedAlerts", 32L);

        when(alertService.getAlertMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/api/alerts/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalAlerts").value(42))
                .andExpect(jsonPath("$.data.openAlerts").value(10))
                .andExpect(jsonPath("$.data.resolvedAlerts").value(32));
    }
}
