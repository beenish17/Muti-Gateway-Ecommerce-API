package com.ecommerce.payment.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(nullable = false)
    private String payerId;

    @Column(nullable = false)
    private String payeeId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private String preferredGateway;

    @Column(nullable = false)
    private PaymentSatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentSatus getStatus() {
        return status;
    }

    public void setStatus(PaymentSatus status) {
        this.status = status;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getPreferredGateway() {
        return preferredGateway;
    }

    public void setPreferredGateway(String preferredGateway) {
        this.preferredGateway = preferredGateway;
    }
}
