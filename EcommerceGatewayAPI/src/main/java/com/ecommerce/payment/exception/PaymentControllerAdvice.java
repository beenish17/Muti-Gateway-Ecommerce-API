package com.ecommerce.payment.exception;

import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.model.PaymentSatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class PaymentControllerAdvice {
    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ResponseEntity<PaymentResponse> handlePaymentProcessingException(
            PaymentProcessingException e) {
        log.error("Error processing payment at timestamp {}: {}", LocalDateTime.now(), e.getMessage(), e);
        var errorResponse = new PaymentResponse();
        errorResponse.setStatus(PaymentSatus.REJECTED);
        errorResponse.setErrorDetails(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public PaymentResponse onIllegalArgumentException(IllegalArgumentException exception) {
        String message = exception.getMessage();
        return new PaymentResponse(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> onMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        return methodArgumentNotValidException.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

}
