package com.store.dao;

import com.store.entity.CartItem;
import com.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemDAO extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProduct(Long cartId, Product product);

    List<CartItem> findByCartId(Long cartId);

    boolean existsByCartIdAndProduct(Long cartId, Product product);
}
