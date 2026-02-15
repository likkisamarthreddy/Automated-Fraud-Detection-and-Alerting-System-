package com.frauddetection.fraudengine.repository;

import com.frauddetection.fraudengine.entity.FraudResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FraudResultRepository extends JpaRepository<FraudResult, Long> {
    Optional<FraudResult> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
    long countByDecision(String decision);
}
