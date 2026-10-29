package com.store.resource;

import com.store.dto.cart.AddCartItemRequest;
import com.store.dto.cart.CartResponse;
import com.store.dto.cart.UpdateCartItemRequest;
import com.store.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartResource {

    private final CartService cartService;

    public CartResource(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.getCart(authentication.getName())
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(
                cartService.addItem(
                        authentication.getName(),
                        request
                )
        );
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateItem(
                        authentication.getName(),
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cartService.removeItem(
                        authentication.getName(),
                        id
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            Authentication authentication
    ) {
        cartService.clearCart(authentication.getName());

        return ResponseEntity.noContent().build();
    }
}