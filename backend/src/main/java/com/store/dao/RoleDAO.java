package com.store.dao;

import com.store.entity.Role;
import com.store.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDAO extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}