package com.store.service;

import com.store.dao.OrderDAO;
import com.store.dao.PaymentDAO;
import com.store.dto.payment.PaymentResponse;
import com.store.dto.payment.PaymentSimulationRequest;
import com.store.entity.Order;
import com.store.entity.Payment;
import com.store.entity.User;
import com.store.entity.enums.PaymentMethod;
import com.store.entity.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentDAO paymentDAO;

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        testUser.setEmail("payer@store.com");

        testOrder = new Order();
        ReflectionTestUtils.setField(testOrder, "id", 10L);
        testOrder.setOrderNumber("ORD-TEST1234");
        testOrder.setUser(testUser);

        testPayment = new Payment();
        ReflectionTestUtils.setField(testPayment, "id", 100L);
        testPayment.setOrder(testOrder);
        testPayment.setPaymentMethod(PaymentMethod.CARD_SIMULATION);
        testPayment.setPaymentStatus(PaymentStatus.PENDING);
        testPayment.setAmount(new BigDecimal("1999.00"));
    }

    @Test
    @DisplayName("simulatePayment - SUCCESS transitions status and sets transaction ref")
    void simulatePayment_whenSuccess_shouldUpdatePayment() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(testOrder));
        when(paymentDAO.findByOrderId(10L)).thenReturn(Optional.of(testPayment));
        when(paymentDAO.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.simulatePayment(
                "payer@store.com",
                10L,
                new PaymentSimulationRequest("SUCCESS")
        );

        assertThat(response).isNotNull();
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.transactionReference()).startsWith("SIM-");
        assertThat(response.paidAt()).isNotNull();
        verify(paymentDAO).save(testPayment);
    }

    @Test
    @DisplayName("simulatePayment - FAILED transitions status to FAILED")
    void simulatePayment_whenFailed_shouldUpdatePaymentToFailed() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(testOrder));
        when(paymentDAO.findByOrderId(10L)).thenReturn(Optional.of(testPayment));
        when(paymentDAO.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.simulatePayment(
                "payer@store.com",
                10L,
                new PaymentSimulationRequest("FAILED")
        );

        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(paymentDAO).save(testPayment);
    }

    @Test
    @DisplayName("simulatePayment - throws 400 when unauthorized user accesses order")
    void simulatePayment_whenUserNotOwner_shouldThrowException() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> paymentService.simulatePayment(
                "stranger@store.com",
                10L,
                new PaymentSimulationRequest("SUCCESS")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You are not allowed to access this order");

        verify(paymentDAO, never()).save(any());
    }

    @Test
    @DisplayName("simulatePayment - throws 400 when payment already processed")
    void simulatePayment_whenAlreadyProcessed_shouldThrowException() {
        testPayment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(orderDAO.findById(10L)).thenReturn(Optional.of(testOrder));
        when(paymentDAO.findByOrderId(10L)).thenReturn(Optional.of(testPayment));

        assertThatThrownBy(() -> paymentService.simulatePayment(
                "payer@store.com",
                10L,
                new PaymentSimulationRequest("SUCCESS")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment has already been processed");

        verify(paymentDAO, never()).save(any());
    }
}
