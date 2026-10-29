package com.store.dao;

import com.store.entity.Address;
import com.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressDAO extends JpaRepository<Address, Long> {

    List<Address> findByUserOrderByCreatedAtDesc(User user);

    Optional<Address> findByIdAndUser(Long id, User user);
}