package com.store.dao;

import com.store.entity.Order;
import com.store.entity.User;
import com.store.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDAO extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}