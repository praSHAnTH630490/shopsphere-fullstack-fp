package com.store.resource;

import com.store.dto.payment.PaymentResponse;
import com.store.dto.payment.PaymentSimulationRequest;
import com.store.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentResource {

    private final PaymentService paymentService;

    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<PaymentResponse> simulatePayment(
            Authentication authentication,
            @RequestParam Long orderId,
            @Valid @RequestBody PaymentSimulationRequest request
    ) {

        return ResponseEntity.ok(
                paymentService.simulatePayment(
                        authentication.getName(),
                        orderId,
                        request
                )
        );
    }
}