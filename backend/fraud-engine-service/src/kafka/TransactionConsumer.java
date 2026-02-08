package com.fraudengine.kafka;

import com.fraudengine.core.FraudEngineService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Map;

@Service
public class TransactionConsumer {

    private final FraudEngineService engine;
    private final StringRedisTemplate redis;

    public TransactionConsumer(FraudEngineService engine,
                               StringRedisTemplate redis) {
        this.engine=engine;
        this.redis=redis;
    }

    @KafkaListener(topics="transactions.main",groupId="fraud-group")
    public void consume(Map<String,Object> txn) {

        String txnId=(String)txn.get("transactionId");
        String key="done:"+txnId;

        if(Boolean.TRUE.equals(redis.hasKey(key))) return;

        String userId=(String)txn.get("userId");
        double amount=(double)txn.get("amount");
        String device=(String)txn.get("device");

        int risk=engine.calculateRisk(userId,amount,device);
        String result=engine.decision(risk);

        redis.opsForValue().set(key,"1");

        System.out.println("Transaction "+txnId+" -> "+result);
    }
}
