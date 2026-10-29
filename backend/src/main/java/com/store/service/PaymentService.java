package com.store.service;

import com.store.dao.OrderDAO;
import com.store.dao.PaymentDAO;
import com.store.dto.payment.PaymentResponse;
import com.store.dto.payment.PaymentSimulationRequest;
import com.store.entity.Order;
import com.store.entity.Payment;
import com.store.entity.enums.OrderStatus;
import com.store.entity.enums.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final OrderDAO orderDAO;

    public PaymentService(
            PaymentDAO paymentDAO,
            OrderDAO orderDAO
    ) {
        this.paymentDAO = paymentDAO;
        this.orderDAO = orderDAO;
    }

    @Transactional
    public PaymentResponse simulatePayment(
            String email,
            Long orderId,
            PaymentSimulationRequest request
    ) {

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        if (!order.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "You are not allowed to access this order"
            );
        }

        Payment payment = paymentDAO.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found"
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Payment has already been processed"
            );
        }

        String result = request.result()
                .trim()
                .toUpperCase();

        if ("SUCCESS".equals(result)) {

            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            payment.setTransactionReference(
                    "SIM-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 12)
                            .toUpperCase()
            );

            payment.setPaidAt(LocalDateTime.now());

        } else if ("FAILED".equals(result)) {

            payment.setPaymentStatus(PaymentStatus.FAILED);

        } else {

            throw new IllegalArgumentException(
                    "Payment result must be SUCCESS or FAILED"
            );
        }

        Payment savedPayment = paymentDAO.save(payment);

        return toResponse(savedPayment);
    }

    private PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getTransactionReference(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}