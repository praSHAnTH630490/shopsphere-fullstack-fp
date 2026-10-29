package com.store.dao;

import com.store.entity.Product;
import com.store.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemDAO extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistIdAndProduct(
            Long wishlistId,
            Product product
    );

    boolean existsByWishlistIdAndProduct(
            Long wishlistId,
            Product product
    );
}