package com.frauddetection.transaction.controller;

import com.frauddetection.common.dto.ApiResponse;
import com.frauddetection.common.dto.TransactionRequest;
import com.frauddetection.transaction.entity.Transaction;
import com.frauddetection.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> submitTransaction(
            @Valid @RequestBody TransactionRequest request) {
        try {
            Transaction transaction = transactionService.submitTransaction(request);
            log.info("Transaction submitted: txnId={}", transaction.getTransactionId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Transaction submitted for fraud analysis", transaction));
        } catch (Exception e) {
            log.error("Failed to submit transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process transaction: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactions(
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "decision", required = false) String decision,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<Transaction> transactions = transactionService.getTransactions(
                userId, decision, startDate, endDate,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable(name = "id") String id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(ApiResponse.success(transaction));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getMetrics()));
    }
}
