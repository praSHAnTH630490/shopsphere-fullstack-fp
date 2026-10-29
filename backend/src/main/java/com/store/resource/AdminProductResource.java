package com.store.resource;

import com.store.dto.admin.AdminProductRequest;
import com.store.dto.catalog.ProductResponse;
import com.store.service.AdminProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductResource {

    private final AdminProductService adminProductService;

    public AdminProductResource(
            AdminProductService adminProductService
    ) {
        this.adminProductService = adminProductService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {

        return ResponseEntity.ok(
                adminProductService.getAllProducts()
        );
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody AdminProductRequest request
    ) {

        return ResponseEntity.ok(
                adminProductService.createProduct(request)
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductRequest request
    ) {

        return ResponseEntity.ok(
                adminProductService.updateProduct(
                        productId,
                        request
                )
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deactivateProduct(
            @PathVariable Long productId
    ) {

        adminProductService.deactivateProduct(productId);

        return ResponseEntity.noContent().build();
    }
}