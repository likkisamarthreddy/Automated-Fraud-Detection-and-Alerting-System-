package com.frauddetection.fraudengine.config;

import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.repository.FraudRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(FraudRuleRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // AMOUNT RULES
                repository.save(new FraudRule(null, "High Amount (>50k)", "AMOUNT", true, 50000, 40,
                        "Alert on high transaction amount", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "Very High Amount (>100k)", "AMOUNT", true, 100000, 70,
                        "High risk transaction amount", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "Amount > 5x User Avg", "AMOUNT", true, 5, 50,
                        "Sudden spike specific to user history", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "Amount > 3x User Avg", "AMOUNT", true, 3, 30,
                        "Deviation from user average", LocalDateTime.now(), LocalDateTime.now()));

                // VELOCITY RULES
                repository.save(new FraudRule(null, "Velocity Burst (>10/60s)", "VELOCITY", true, 10, 70,
                        "Extreme transaction frequency", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "Velocity Warning (>5/60s)", "VELOCITY", true, 5, 40,
                        "High transaction frequency", LocalDateTime.now(), LocalDateTime.now()));

                // DEVICE RULES
                repository.save(new FraudRule(null, "New Device Check", "DEVICE", true, 0, 30,
                        "Transaction from unknown device", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "Multi-Device Burst", "DEVICE", true, 3, 60,
                        ">3 distinct devices in 10 mins", LocalDateTime.now(), LocalDateTime.now()));

                // BEHAVIORAL
                repository.save(new FraudRule(null, "Dormant Account", "BEHAVIORAL", true, 90, 50,
                        "Reactivation after 90 days silence", LocalDateTime.now(), LocalDateTime.now()));
                repository.save(new FraudRule(null, "High Risk Time (1am-4am)", "BEHAVIORAL", true, 0, 25,
                        "Transaction during unusual hours", LocalDateTime.now(), LocalDateTime.now()));

                // META
                repository.save(new FraudRule(null, "Risk Stacking Bonus", "META", true, 3, 20,
                        "Bonus score if 3+ rules trigger", LocalDateTime.now(), LocalDateTime.now()));
            }
        };
    }
}
