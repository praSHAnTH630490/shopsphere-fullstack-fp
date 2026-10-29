package com.store.dto.auth;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        Set<String> roles,
        boolean active,
        LocalDateTime createdAt
) {
}