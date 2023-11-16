package com.ecommerce.payment.repository;

import com.ecommerce.payment.model.Currency;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.PaymentSatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Payment p " +
            "WHERE p.payerId = :payerId " +
            "AND p.payeeId = :payeeId " +
            "AND p.amount = :amount " +
            "AND p.currency = :currency " +
            "AND p.preferredGateway = :preferredGateway " +
            "AND p.status = :status " +
            "AND p.timestamp BETWEEN :startTime AND :endTime")
    boolean isTransactionExist(
            String payerId,
            String payeeId,
            Double amount,
            Currency currency,
            String preferredGateway,
            PaymentSatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
