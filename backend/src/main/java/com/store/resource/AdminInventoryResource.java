package com.store.resource;

import com.store.dto.admin.AdminInventoryRequest;
import com.store.dto.admin.AdminInventoryResponse;
import com.store.service.AdminInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryResource {

    private final AdminInventoryService adminInventoryService;

    public AdminInventoryResource(
            AdminInventoryService adminInventoryService
    ) {
        this.adminInventoryService = adminInventoryService;
    }

    @GetMapping
    public ResponseEntity<List<AdminInventoryResponse>> getInventory() {

        return ResponseEntity.ok(
                adminInventoryService.getInventory()
        );
    }

    @PatchMapping("/products/{productId}")
    public ResponseEntity<AdminInventoryResponse> updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody AdminInventoryRequest request
    ) {

        return ResponseEntity.ok(
                adminInventoryService.updateStock(
                        productId,
                        request
                )
        );
    }
}