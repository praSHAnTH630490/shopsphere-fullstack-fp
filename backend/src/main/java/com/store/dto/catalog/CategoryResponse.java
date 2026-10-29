package com.store.dto.catalog;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        boolean active,
        LocalDateTime createdAt
) {}