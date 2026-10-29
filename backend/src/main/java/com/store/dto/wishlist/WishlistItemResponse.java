package com.store.dto.wishlist;

import java.math.BigDecimal;

public record WishlistItemResponse(
        Long productId,
        String productName,
        String imageUrl,
        BigDecimal price,
        boolean active
) {}