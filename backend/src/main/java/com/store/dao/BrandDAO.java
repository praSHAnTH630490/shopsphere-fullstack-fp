package com.store.dao;

import com.store.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandDAO extends JpaRepository<Brand, Long> {

    List<Brand> findByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}