package com.store.dto.catalog;

import java.time.LocalDateTime;

public record BrandResponse(
        Long id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt
) {}