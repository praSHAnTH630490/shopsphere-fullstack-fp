package com.store.dao;

import com.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
}