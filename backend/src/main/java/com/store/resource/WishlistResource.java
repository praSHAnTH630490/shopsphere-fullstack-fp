package com.store.resource;

import com.store.dto.wishlist.WishlistResponse;
import com.store.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistResource {

    private final WishlistService wishlistService;

    public WishlistResource(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                wishlistService.getWishlist(
                        authentication.getName()
                )
        );
    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<WishlistResponse> addProduct(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                wishlistService.addProduct(
                        authentication.getName(),
                        productId
                )
        );
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<WishlistResponse> removeProduct(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                wishlistService.removeProduct(
                        authentication.getName(),
                        productId
                )
        );
    }
}