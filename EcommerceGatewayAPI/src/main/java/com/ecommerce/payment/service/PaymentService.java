package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(PaymentRequest payment);
}
