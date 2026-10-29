package com.store.dto.wishlist;

import java.util.List;

public record WishlistResponse(
        Long wishlistId,
        List<WishlistItemResponse> items
) {}