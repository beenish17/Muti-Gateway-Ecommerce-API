package com.ecommerce.payment.dto;
import com.ecommerce.payment.model.PaymentSatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private PaymentSatus status;
    private String transactionId;
    private String errorDetails;
    private Map<String, String> validationErrors;

    public PaymentResponse(PaymentSatus status, String transactionId) {
        this.status = status;
        this.transactionId = transactionId;
    }

    public PaymentResponse(String errorDetails) {
        this.errorDetails = errorDetails;
    }
    public PaymentResponse(PaymentSatus status, String transactionId, String errorDetails) {
        this.status = status;
        this.transactionId = transactionId;
        this.errorDetails = errorDetails;
    }


    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
    public PaymentResponse() {
    }

    public PaymentSatus getStatus() {
        return status;
    }

    public void setStatus(PaymentSatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
}
