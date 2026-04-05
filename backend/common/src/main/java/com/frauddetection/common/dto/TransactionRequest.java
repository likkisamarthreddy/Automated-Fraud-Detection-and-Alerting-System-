package com.frauddetection.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @Positive(message = "amount must be positive")
    private double amount;

    @NotBlank(message = "device is required")
    private String device;

    public TransactionRequest() {}

    public TransactionRequest(String userId, double amount, String device) {
        this.userId = userId;
        this.amount = amount;
        this.device = device;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
}
