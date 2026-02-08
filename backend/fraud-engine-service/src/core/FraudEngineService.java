package com.fraudengine.core;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class FraudEngineService {

    private final StringRedisTemplate redis;

    public FraudEngineService(StringRedisTemplate redis) {
        this.redis=redis;
    }

    public int calculateRisk(String userId,double amount,String device) {
        int risk=0;

        if(amount>10000) risk+=60;
        if(device.equals("new")) risk+=30;

        String key="vel:"+userId;
        Long count=redis.opsForValue().increment(key);
        redis.expire(key,60,TimeUnit.SECONDS);

        if(count!=null && count>5) risk+=40;

        return risk;
    }

    public String decision(int risk) {
        if(risk>=80) return "BLOCK";
        if(risk>=50) return "ALERT";
        return "ALLOW";
    }
}
