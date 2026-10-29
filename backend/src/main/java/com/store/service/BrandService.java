package com.store.service;

import com.store.dao.BrandDAO;
import com.store.dto.catalog.BrandResponse;
import com.store.entity.Brand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    private final BrandDAO brandDAO;

    public BrandService(BrandDAO brandDAO) {
        this.brandDAO = brandDAO;
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> getActiveBrands() {

        return brandDAO.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrand(Long id) {

        Brand brand = brandDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Brand not found")
                );

        if (!brand.isActive()) {
            throw new IllegalArgumentException("Brand is not active");
        }

        return toResponse(brand);
    }

    private BrandResponse toResponse(Brand brand) {

        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.isActive(),
                brand.getCreatedAt()
        );
    }
}