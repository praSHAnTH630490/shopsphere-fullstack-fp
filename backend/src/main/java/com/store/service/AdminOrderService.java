package com.store.service;

import com.store.dao.OrderDAO;
import com.store.dao.PaymentDAO;
import com.store.dto.admin.AdminOrderStatusRequest;
import com.store.dto.order.OrderItemResponse;
import com.store.dto.order.OrderResponse;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Payment;
import com.store.entity.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;

    public AdminOrderService(
            OrderDAO orderDAO,
            PaymentDAO paymentDAO
    ) {
        this.orderDAO = orderDAO;
        this.paymentDAO = paymentDAO;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderDAO.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            AdminOrderStatusRequest request
    ) {

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.status();

        validateStatusChange(currentStatus, newStatus);

        order.setStatus(newStatus);

        Order savedOrder = orderDAO.save(order);

        return toResponse(savedOrder);
    }

    private void validateStatusChange(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Order already has this status"
            );
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled order cannot be updated"
            );
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "Delivered order cannot be updated"
            );
        }

        if (newStatus == OrderStatus.PLACED) {
            throw new IllegalArgumentException(
                    "Order cannot be moved back to PLACED"
            );
        }

        if (currentStatus == OrderStatus.PLACED
                && newStatus != OrderStatus.CONFIRMED
                && newStatus != OrderStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "PLACED order can only move to CONFIRMED or CANCELLED"
            );
        }

        if (currentStatus == OrderStatus.CONFIRMED
                && newStatus != OrderStatus.PACKED
                && newStatus != OrderStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "CONFIRMED order can only move to PACKED or CANCELLED"
            );
        }

        if (currentStatus == OrderStatus.PACKED
                && newStatus != OrderStatus.OUT_FOR_DELIVERY) {

            throw new IllegalArgumentException(
                    "PACKED order can only move to OUT_FOR_DELIVERY"
            );
        }

        if (currentStatus == OrderStatus.OUT_FOR_DELIVERY
                && newStatus != OrderStatus.DELIVERED) {

            throw new IllegalArgumentException(
                    "OUT_FOR_DELIVERY order can only move to DELIVERED"
            );
        }
    }

    private OrderResponse toResponse(Order order) {

        Payment payment = paymentDAO
                .findByOrderId(order.getId())
                .orElse(null);

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getAddress().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                payment != null
                        ? payment.getPaymentMethod()
                        : null,
                payment != null
                        ? payment.getPaymentStatus()
                        : null,
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}