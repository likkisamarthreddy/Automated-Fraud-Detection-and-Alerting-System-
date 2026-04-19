package com.frauddetection.alert.controller;

import com.frauddetection.alert.entity.Alert;
import com.frauddetection.alert.service.AlertService;
import com.frauddetection.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Alert>>> getAlerts(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<Alert> alerts;
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null && !status.isBlank()) {
            alerts = alertService.getAlertsByStatus(status.toUpperCase(), pageable);
        } else {
            alerts = alertService.getAlerts(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponse<Alert>> blockAlert(@PathVariable(name = "id") Long id) {
        try {
            Alert alert = alertService.updateAlertStatus(id, "BLOCKED");
            return ResponseEntity.ok(ApiResponse.success("Transaction blocked", alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/allow")
    public ResponseEntity<ApiResponse<Alert>> allowAlert(@PathVariable(name = "id") Long id) {
        try {
            Alert alert = alertService.updateAlertStatus(id, "ALLOWED");
            return ResponseEntity.ok(ApiResponse.success("Transaction allowed", alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Alert>> reviewAlert(@PathVariable(name = "id") Long id) {
        try {
            Alert alert = alertService.updateAlertStatus(id, "UNDER_REVIEW");
            return ResponseEntity.ok(ApiResponse.success("Transaction under review", alert));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertMetrics()));
    }
}
