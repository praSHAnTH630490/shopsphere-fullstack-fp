package com.store.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentSimulationRequest(

        @NotBlank(message = "Payment result is required")
        String result
) {
}