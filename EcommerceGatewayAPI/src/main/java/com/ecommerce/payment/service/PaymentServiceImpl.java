package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.exception.PaymentProcessingException;
import com.ecommerce.payment.model.Gateway;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.PaymentSatus;
import com.ecommerce.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private static final int PAYMENT_PROCESSING_TIMEOUT_SECONDS = 10;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    @Override
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            if (isTransactionAlreadyExists(paymentRequest)) {
                throw new IllegalStateException("Transaction with the same ID already exists.");
            }
            paymentResponse.setStatus(PaymentSatus.INITIATED);
            var paymentSuccess = process(paymentRequest, paymentRequest.getPreferredGateway());
            if (!paymentSuccess) {
                String alternativeGateway = getAlternativeGateway(paymentRequest.getPreferredGateway());
                paymentSuccess = process(paymentRequest, alternativeGateway);
            }

            if (paymentSuccess) {
                paymentResponse.setStatus(PaymentSatus.DONE);
            } else {
                paymentResponse.setStatus(PaymentSatus.REJECTED);
                logger.error("Payment failed after retrying. Fallback to default behavior for payment: {}", paymentRequest.getPayerId());
                fallbackToDefaultBehavior(paymentRequest);
            }

            var payment = transformPayment(paymentRequest);
            payment.setStatus(paymentResponse.getStatus());
            paymentRepository.save(payment);
            logger.info("Payment completed for Transaction ID: {}", paymentResponse.getTransactionId());
            paymentResponse.setTransactionId(payment.getTransactionId().toString());
            return paymentResponse;

        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            throw new PaymentProcessingException("Error processing payment", e);
        }
    }

    private boolean isTransactionAlreadyExists(PaymentRequest paymentRequest) {
        var timestamp = LocalDateTime.now();
        var tolerance = Duration.ofMinutes(1);
        var startTime = timestamp.minus(tolerance);

        logger.debug("Checking if a transaction with details exists: Payer ID {}, Payee ID {}, Amount {}, Currency {}, Preferred Gateway {}, Timestamp between {} and {}",
                paymentRequest.getPayerId(), paymentRequest.getPayeeId(),
                paymentRequest.getAmount(), paymentRequest.getCurrency(),paymentRequest.getPreferredGateway(),
                tolerance, timestamp
        );

        return paymentRepository.isTransactionExist(
                paymentRequest.getPayerId(),
                paymentRequest.getPayeeId(),
                paymentRequest.getAmount(),
                paymentRequest.getCurrency(),
                paymentRequest.getPreferredGateway(),
                PaymentSatus.DONE,
                startTime,
                timestamp
        );
    }
    private String getAlternativeGateway(String preferredGateway) {
        return Arrays.stream(Gateway.values())
                .filter(gateway -> gateway.name().equals(preferredGateway))
                .findFirst()
                .map(gateway -> {
                    switch (gateway) {
                        case STRIPE:
                            return Gateway.PAYPAL.name();
                        case PAYPAL:
                            return Gateway.STRIPE.name();
                        default:
                            throw new IllegalArgumentException("Unsupported gateway: " + preferredGateway);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Unsupported gateway: " + preferredGateway));
    }

    private boolean process(PaymentRequest paymentRequest, String gateway) {
        try {
            CompletableFuture<Boolean> paymentFuture = CompletableFuture.supplyAsync(() ->
                    simulatePaymentProcessing(transformPayment(paymentRequest), gateway));
            return paymentFuture.orTimeout(PAYMENT_PROCESSING_TIMEOUT_SECONDS, TimeUnit.SECONDS).join();
        } catch (Exception e) {
            logger.error("Error processing payment with gateway {}: {}", gateway, e.getMessage());
            return false;
        }
    }

    private boolean simulatePaymentProcessing(Payment payment, String gateway) {
        return Math.random() < 0.8; // 80% success rate
    }

    private Payment transformPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setPayerId(paymentRequest.getPayerId());
        payment.setPayeeId(paymentRequest.getPayeeId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPreferredGateway(paymentRequest.getPreferredGateway());
        payment.setTimestamp(LocalDateTime.now());
        return payment;
    }

    private void fallbackToDefaultBehavior(PaymentRequest paymentRequest) {
      logger.error("Payment failed after retrying. Fallback to default behavior for payment: {}", paymentRequest.getPayerId());
        sendNotificationToAdministrators(paymentRequest);
    }

    private void sendNotificationToAdministrators(PaymentRequest paymentRequest) {
        String subject = "Payment Processing Failure Notification";
        String message = "Payment for PayerId " + paymentRequest.getPayerId() + " failed after retrying. Fallback to default behavior.";
        // emailService.sendEmail(adminEmail, subject, message);
    }

}
