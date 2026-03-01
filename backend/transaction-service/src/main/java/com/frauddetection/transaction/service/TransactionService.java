package com.frauddetection.transaction.service;

import com.frauddetection.common.dto.TransactionEvent;
import com.frauddetection.common.dto.TransactionRequest;
import com.frauddetection.transaction.entity.Transaction;
import com.frauddetection.transaction.kafka.TransactionProducer;
import com.frauddetection.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionProducer transactionProducer) {
        this.transactionRepository = transactionRepository;
        this.transactionProducer = transactionProducer;
    }

    @Transactional
    public Transaction submitTransaction(TransactionRequest request) {
        String transactionId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        Transaction transaction = new Transaction(
                transactionId,
                request.getUserId(),
                BigDecimal.valueOf(request.getAmount()),
                request.getDevice(),
                timestamp
        );

        transactionRepository.save(transaction);
        log.info("Transaction persisted: txnId={}, userId={}, amount={}",
                transactionId, request.getUserId(), request.getAmount());

        TransactionEvent event = new TransactionEvent(
                transactionId,
                request.getUserId(),
                request.getAmount(),
                request.getDevice(),
                timestamp
        );
        transactionProducer.publish(event);

        return transaction;
    }

    public Page<Transaction> getTransactions(String userId, String decision,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              Pageable pageable) {
        return transactionRepository.findWithFilters(userId, decision, startDate, endDate, pageable);
    }

    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTransactions", transactionRepository.count());
        metrics.put("blockedCount", transactionRepository.countByDecision("BLOCK"));
        metrics.put("alertCount", transactionRepository.countByDecision("ALERT"));
        metrics.put("allowedCount", transactionRepository.countByDecision("ALLOW"));

        long total = transactionRepository.count();
        long fraudCount = transactionRepository.countByDecision("BLOCK") +
                          transactionRepository.countByDecision("ALERT");
        double fraudRate = total > 0 ? (double) fraudCount / total * 100 : 0.0;
        metrics.put("fraudRate", Math.round(fraudRate * 100.0) / 100.0);

        return metrics;
    }
}
