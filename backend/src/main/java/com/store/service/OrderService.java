package com.store.service;

import com.store.dao.AddressDAO;
import com.store.dao.CartDAO;
import com.store.dao.CartItemDAO;
import com.store.dao.OrderDAO;
import com.store.dao.OrderItemDAO;
import com.store.dao.PaymentDAO;
import com.store.dao.ProductDAO;
import com.store.dao.UserDAO;
import com.store.dto.order.CreateOrderRequest;
import com.store.dto.order.OrderItemResponse;
import com.store.dto.order.OrderResponse;
import com.store.entity.Address;
import com.store.entity.Cart;
import com.store.entity.CartItem;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Payment;
import com.store.entity.Product;
import com.store.entity.User;
import com.store.entity.enums.OrderStatus;
import com.store.entity.enums.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final UserDAO userDAO;
    private final AddressDAO addressDAO;
    private final CartDAO cartDAO;
    private final CartItemDAO cartItemDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PaymentDAO paymentDAO;

    public OrderService(
            UserDAO userDAO,
            AddressDAO addressDAO,
            CartDAO cartDAO,
            CartItemDAO cartItemDAO,
            ProductDAO productDAO,
            OrderDAO orderDAO,
            OrderItemDAO orderItemDAO,
            PaymentDAO paymentDAO
    ) {
        this.userDAO = userDAO;
        this.addressDAO = addressDAO;
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.paymentDAO = paymentDAO;
    }

    @Transactional
    public OrderResponse createOrder(
            String email,
            CreateOrderRequest request
    ) {

        User user = getUser(email);

        Address address = addressDAO
                .findByIdAndUser(request.addressId(), user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Address not found"
                        )
                );

        Cart cart = cartDAO
                .findByUser(user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found"
                        )
                );

        List<CartItem> cartItems = cartItemDAO.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot place an order with an empty cart"
            );
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = productDAO
                    .findById(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Product not found"
                              )
                    );

            if (!product.isActive()) {
                throw new IllegalArgumentException(
                        "Product is no longer available: "
                                + product.getName()
                );
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            BigDecimal unitPrice = product.getPrice();

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            productDAO.save(product);
        }

        Order order = new Order();

        order.setUser(user);
        order.setAddress(address);
        order.setOrderNumber(generateOrderNumber());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);

        Order savedOrder = orderDAO.save(order);

        for (OrderItem orderItem : orderItems) {

            orderItem.setOrder(savedOrder);

            orderItemDAO.save(orderItem);
        }

        Payment payment = new Payment();

        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(totalAmount);

        paymentDAO.save(payment);

        cartItemDAO.deleteAll(cartItems);

        return toResponse(
                savedOrder,
                orderItems,
                payment
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String email) {

        User user = getUser(email);

        return orderDAO
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(
            String email,
            Long orderId
    ) {

        User user = getUser(email);

        Order order = orderDAO
                .findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to access this order"
            );
        }

        return toOrderResponse(order);
    }

    private String generateOrderNumber() {

        return "ORD-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    @Transactional
    public OrderResponse cancelOrder(
            String email,
            Long orderId
    ) {

        User user = getUser(email);

        Order order = orderDAO
                .findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to cancel this order"
            );
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalArgumentException(
                    "Only placed orders can be cancelled"
            );
        }

        List<OrderItem> orderItems =
                orderItemDAO.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {

            Product product = productDAO
                    .findById(orderItem.getProduct().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Product not found"
                            )
                    );

            product.setStockQuantity(
                    product.getStockQuantity()
                            + orderItem.getQuantity()
            );

            productDAO.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderDAO.save(order);

        return toOrderResponse(savedOrder);
    }

    private User getUser(String email) {

        return userDAO
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private OrderResponse toResponse(
            Order order,
            List<OrderItem> orderItems,
            Payment payment
    ) {

        List<OrderItemResponse> items = orderItems
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getAddress().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderResponse toOrderResponse(Order order) {

        List<OrderItem> orderItems =
                orderItemDAO.findByOrderId(order.getId());

        Payment payment =
                paymentDAO.findByOrderId(order.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found"
                                )
                        );

        List<OrderItemResponse> items = orderItems
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getAddress().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
