package com.frauddetection.alert.repository;

import com.frauddetection.alert.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByStatus(String status, Pageable pageable);
    Page<Alert> findByUserId(String userId, Pageable pageable);
    long countByUserIdAndCreatedAtAfter(String userId, LocalDateTime after);
    long countByStatus(String status);
}
