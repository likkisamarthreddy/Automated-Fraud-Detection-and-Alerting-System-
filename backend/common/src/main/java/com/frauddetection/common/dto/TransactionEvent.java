package com.frauddetection.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionEvent implements Serializable {

    private String transactionId;
    private String userId;
    private double amount;
    private String device;
    private long timestamp;

    public TransactionEvent() {
    }

    public TransactionEvent(String transactionId, String userId, double amount, String device, long timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.device = device;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TransactionEvent{transactionId='" + transactionId + "', userId='" + userId +
                "', amount=" + amount + ", device='" + device + "'}";
    }
}
