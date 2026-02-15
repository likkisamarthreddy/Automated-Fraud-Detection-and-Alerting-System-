package com.frauddetection.fraudengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class VelocityService {

    private static final Logger log = LoggerFactory.getLogger(VelocityService.class);
    private static final String PROFILE_PREFIX = "profile:";
    private static final String HISTORY_PREFIX = "history:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fraud.velocity.ttl-seconds:86400}") // Default 1 day for demo, production would use DB
    private long ttlSeconds;

    public VelocityService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public UserProfile getUserProfile(String userId) {
        String key = PROFILE_PREFIX + userId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if (data.isEmpty()) {
            return new UserProfile();
        }

        UserProfile profile = new UserProfile();
        profile.setTotalAmount(Double.parseDouble((String) data.getOrDefault("totalAmount", "0")));
        profile.setTransactionCount(Integer.parseInt((String) data.getOrDefault("transactionCount", "0")));
        profile.setLastTransactionTime(Long.parseLong((String) data.getOrDefault("lastTransactionTime", "0")));
        profile.setFailedAttempts(Integer.parseInt((String) data.getOrDefault("failedAttempts", "0")));

        // Devices (stored as comma-separated in Redis for simplicity in this demo)
        String devices = (String) data.getOrDefault("devices", "");
        if (!devices.isEmpty()) {
            profile.setKnownDevices(new HashSet<>(Arrays.asList(devices.split(","))));
        }

        return profile;
    }

    public void updateUserProfile(String userId, double amount, String device, long timestamp, boolean isFailure) {
        String key = PROFILE_PREFIX + userId;

        if (isFailure) {
            redisTemplate.opsForHash().increment(key, "failedAttempts", 1);
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
            return;
        }

        // Update stats
        redisTemplate.opsForHash().increment(key, "totalAmount", amount);
        redisTemplate.opsForHash().increment(key, "transactionCount", 1);
        redisTemplate.opsForHash().put(key, "lastTransactionTime", String.valueOf(timestamp));
        redisTemplate.opsForHash().put(key, "failedAttempts", "0"); // Reset on success

        // Update devices
        UserProfile current = getUserProfile(userId);
        Set<String> devices = current.getKnownDevices();
        devices.add(device);
        redisTemplate.opsForHash().put(key, "devices", String.join(",", devices));

        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));

        // Add to recent history list for velocity checks
        String histKey = HISTORY_PREFIX + userId;
        try {
            TransactionRecord record = new TransactionRecord(amount, timestamp, device);
            String json = objectMapper.writeValueAsString(record);
            redisTemplate.opsForList().rightPush(histKey, json);
            redisTemplate.opsForList().trim(histKey, -20, -1); // Keep last 20
            redisTemplate.expire(histKey, Duration.ofSeconds(600)); // 10 min window for velocity
        } catch (JsonProcessingException e) {
            log.error("Error saving history", e);
        }
    }

    public List<TransactionRecord> getRecentTransactions(String userId) {
        String histKey = HISTORY_PREFIX + userId;
        List<String> list = redisTemplate.opsForList().range(histKey, 0, -1);
        if (list == null)
            return new ArrayList<>();

        return list.stream().map(json -> {
            try {
                return objectMapper.readValue(json, TransactionRecord.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public static class UserProfile {
        private double totalAmount = 0;
        private int transactionCount = 0;
        private Set<String> knownDevices = new HashSet<>();
        private long lastTransactionTime = 0;
        private int failedAttempts = 0;

        public double getAverageAmount() {
            return transactionCount == 0 ? 0 : totalAmount / transactionCount;
        }

        // Getters and Setters
        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
        }

        public Set<String> getKnownDevices() {
            return knownDevices;
        }

        public void setKnownDevices(Set<String> knownDevices) {
            this.knownDevices = knownDevices;
        }

        public long getLastTransactionTime() {
            return lastTransactionTime;
        }

        public void setLastTransactionTime(long lastTransactionTime) {
            this.lastTransactionTime = lastTransactionTime;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public void setFailedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
        }
    }

    public static class TransactionRecord {
        public double amount;
        public long timestamp;
        public String device;

        public TransactionRecord() {
        }

        public TransactionRecord(double amount, long timestamp, String device) {
            this.amount = amount;
            this.timestamp = timestamp;
            this.device = device;
        }
    }
}
