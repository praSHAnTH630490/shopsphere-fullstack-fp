package com.store.dao;

import com.store.entity.ContactMessage;
import com.store.entity.enums.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageDAO extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findByStatusOrderByCreatedAtDesc(
            ContactStatus status
    );

    List<ContactMessage> findAllByOrderByCreatedAtDesc();
}