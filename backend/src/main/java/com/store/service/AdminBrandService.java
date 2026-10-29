package com.store.service;

import com.store.dao.BrandDAO;
import com.store.dto.admin.AdminBrandRequest;
import com.store.dto.catalog.BrandResponse;
import com.store.entity.Brand;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBrandService {

    private final BrandDAO brandDAO;

    public AdminBrandService(BrandDAO brandDAO) {
        this.brandDAO = brandDAO;
    }

    public List<BrandResponse> getAllBrands() {

        return brandDAO.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BrandResponse createBrand(
            AdminBrandRequest request
    ) {

        String name = request.name().trim();

        if (brandDAO.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Brand name already exists"
            );
        }

        Brand brand = new Brand();

        brand.setName(name);
        brand.setDescription(request.description());
        brand.setActive(true);

        Brand savedBrand = brandDAO.save(brand);

        return toResponse(savedBrand);
    }

    @Transactional
    public BrandResponse updateBrand(
            Long brandId,
            AdminBrandRequest request
    ) {

        Brand brand = brandDAO.findById(brandId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Brand not found"
                        )
                );

        String requestedName = request.name().trim();

        if (!brand.getName().equalsIgnoreCase(requestedName)
                && brandDAO.existsByNameIgnoreCase(requestedName)) {

            throw new IllegalArgumentException(
                    "Brand name already exists"
            );
        }

        brand.setName(requestedName);
        brand.setDescription(request.description());

        Brand savedBrand = brandDAO.save(brand);

        return toResponse(savedBrand);
    }

    @Transactional
    public void deactivateBrand(Long brandId) {

        Brand brand = brandDAO.findById(brandId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Brand not found"
                        )
                );

        brand.setActive(false);

        brandDAO.save(brand);
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