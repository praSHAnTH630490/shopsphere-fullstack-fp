package com.store.dao;

import com.store.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentDAO extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
}