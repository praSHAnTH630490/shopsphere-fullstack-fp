package com.store.dto.admin;

public record AdminInventoryResponse(

        Long productId,
        String productName,
        String sku,
        Integer stockQuantity,
        boolean active

) {
}