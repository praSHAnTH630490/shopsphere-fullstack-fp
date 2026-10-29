package com.store.dao;

import com.store.entity.User;
import com.store.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistDAO extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser(User user);

    boolean existsByUser(User user);
}