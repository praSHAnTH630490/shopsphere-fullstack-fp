package com.store.dto.order;

import com.store.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(

        @NotNull(message = "Address ID is required")
        Long addressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}