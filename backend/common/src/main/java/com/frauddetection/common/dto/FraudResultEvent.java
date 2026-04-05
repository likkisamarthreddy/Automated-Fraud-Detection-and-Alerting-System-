package com.frauddetection.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FraudResultEvent implements Serializable {

    private String transactionId;
    private String userId;
    private double amount;
    private String device;
    private int riskScore;
    private String decision;
    private String reason;

    public FraudResultEvent() {}

    public FraudResultEvent(String transactionId, String userId, double amount, String device,
                            int riskScore, String decision, String reason) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.device = device;
        this.riskScore = riskScore;
        this.decision = decision;
        this.reason = reason;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "FraudResultEvent{txnId='" + transactionId + "', risk=" + riskScore +
               ", decision='" + decision + "'}";
    }
}
