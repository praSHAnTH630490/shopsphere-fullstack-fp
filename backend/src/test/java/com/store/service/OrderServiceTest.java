package com.store.service;

import com.store.dao.*;
import com.store.dto.order.CreateOrderRequest;
import com.store.dto.order.OrderResponse;
import com.store.entity.*;
import com.store.entity.enums.OrderStatus;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private AddressDAO addressDAO;

    @Mock
    private CartDAO cartDAO;

    @Mock
    private CartItemDAO cartItemDAO;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private OrderItemDAO orderItemDAO;

    @Mock
    private PaymentDAO paymentDAO;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Address testAddress;
    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        testUser.setEmail("buyer@store.com");

        testAddress = new Address();
        ReflectionTestUtils.setField(testAddress, "id", 5L);
        testAddress.setUser(testUser);

        testCart = new Cart();
        ReflectionTestUtils.setField(testCart, "id", 10L);
        testCart.setUser(testUser);

        testProduct = new Product();
        ReflectionTestUtils.setField(testProduct, "id", 100L);
        testProduct.setName("Performance Sneakers");
        testProduct.setPrice(new BigDecimal("4999.00"));
        testProduct.setStockQuantity(20);
        testProduct.setActive(true);

        testCartItem = new CartItem();
        ReflectionTestUtils.setField(testCartItem, "id", 50L);
        testCartItem.setCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
    }

    @Test
    @DisplayName("createOrder - successfully creates order, deducts stock, and clears cart")
    void createOrder_whenCartValid_shouldCreateOrder() {
        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(addressDAO.findByIdAndUser(5L, testUser)).thenReturn(Optional.of(testAddress));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findByCartId(10L)).thenReturn(List.of(testCartItem));
        when(productDAO.findById(100L)).thenReturn(Optional.of(testProduct));

        when(orderDAO.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 200L);
            return o;
        });

        CreateOrderRequest request = new CreateOrderRequest(5L, PaymentMethod.CARD_SIMULATION);
        OrderResponse response = orderService.createOrder("buyer@store.com", request);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("9998.00"));
        assertThat(testProduct.getStockQuantity()).isEqualTo(18); // 20 - 2

        verify(orderDAO).save(any(Order.class));
        verify(paymentDAO).save(any(Payment.class));
        verify(cartItemDAO).deleteAll(List.of(testCartItem));
    }

    @Test
    @DisplayName("createOrder - should throw 400 when cart is empty")
    void createOrder_whenCartIsEmpty_shouldThrowException() {
        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(addressDAO.findByIdAndUser(5L, testUser)).thenReturn(Optional.of(testAddress));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findByCartId(10L)).thenReturn(Collections.emptyList());

        CreateOrderRequest request = new CreateOrderRequest(5L, PaymentMethod.CASH_ON_DELIVERY);

        assertThatThrownBy(() -> orderService.createOrder("buyer@store.com", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot place an order with an empty cart");

        verify(orderDAO, never()).save(any());
    }

    @Test
    @DisplayName("createOrder - should throw 400 when requested quantity exceeds available stock")
    void createOrder_whenInsufficientStock_shouldThrowException() {
        testCartItem.setQuantity(25); // exceeds stock of 20

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(addressDAO.findByIdAndUser(5L, testUser)).thenReturn(Optional.of(testAddress));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findByCartId(10L)).thenReturn(List.of(testCartItem));
        when(productDAO.findById(100L)).thenReturn(Optional.of(testProduct));

        CreateOrderRequest request = new CreateOrderRequest(5L, PaymentMethod.CASH_ON_DELIVERY);

        assertThatThrownBy(() -> orderService.createOrder("buyer@store.com", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock for product");

        verify(orderDAO, never()).save(any());
    }

    @Test
    @DisplayName("cancelOrder - should restore product stock and mark order cancelled")
    void cancelOrder_whenOrderPlaced_shouldRestoreStockAndCancel() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 200L);
        order.setUser(testUser);
        order.setAddress(testAddress);
        order.setStatus(OrderStatus.PLACED);

        OrderItem item = new OrderItem();
        ReflectionTestUtils.setField(item, "id", 500L);
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setQuantity(2);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(orderDAO.findById(200L)).thenReturn(Optional.of(order));
        when(orderItemDAO.findByOrderId(200L)).thenReturn(List.of(item));
        when(productDAO.findById(100L)).thenReturn(Optional.of(testProduct));
        when(paymentDAO.findByOrderId(200L)).thenReturn(Optional.of(payment));
        when(orderDAO.save(order)).thenReturn(order);

        OrderResponse response = orderService.cancelOrder("buyer@store.com", 200L);

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(testProduct.getStockQuantity()).isEqualTo(22); // 20 + 2 restored
        verify(productDAO).save(testProduct);
    }
}
