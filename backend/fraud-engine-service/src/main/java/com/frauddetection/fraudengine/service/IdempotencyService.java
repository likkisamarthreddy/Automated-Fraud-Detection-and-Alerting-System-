package com.frauddetection.fraudengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private static final String KEY_PREFIX = "done:";

    private final StringRedisTemplate redisTemplate;

    @Value("${fraud.idempotency.ttl-hours:24}")
    private long ttlHours;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAlreadyProcessed(String transactionId) {
        String key = KEY_PREFIX + transactionId;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            log.info("Duplicate transaction skipped: txnId={}", transactionId);
            return true;
        }
        return false;
    }

    public void markAsProcessed(String transactionId) {
        String key = KEY_PREFIX + transactionId;
        redisTemplate.opsForValue().set(key, "1", Duration.ofHours(ttlHours));
        log.debug("Transaction marked as processed: txnId={}, ttl={}h", transactionId, ttlHours);
    }
}
