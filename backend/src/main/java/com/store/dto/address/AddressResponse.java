package com.store.dto.address;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id,
        String fullName,
        String phone,
        String addressLine,
        String city,
        String state,
        String postalCode,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}