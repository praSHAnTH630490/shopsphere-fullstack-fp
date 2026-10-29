package com.store.dto.payment;

import com.store.entity.enums.PaymentMethod;
import com.store.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        String orderNumber,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String transactionReference,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}