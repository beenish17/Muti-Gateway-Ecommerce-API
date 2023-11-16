package com.ecommerce.payment.dto;

import com.ecommerce.payment.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class PaymentRequest implements Serializable {

    @NotNull(message = "Payer ID is required")
    private String payerId;

    @NotNull(message = "Payee ID is required")
    private String payeeId;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    //@NotNull(message = "Preferred Gateway is required")
    @NotBlank
    private String preferredGateway;

    @NotNull(message = "Currency is required")
    private Currency currency;
}
