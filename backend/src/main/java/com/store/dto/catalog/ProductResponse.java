package com.store.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        String sku,
        boolean active,
        LocalDateTime createdAt
) {}