package com.store.resource;

import com.store.dto.ApiResponse;
import com.store.dto.admin.AdminBrandRequest;
import com.store.dto.catalog.BrandResponse;
import com.store.service.AdminBrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brands")
public class AdminBrandResource {

    private final AdminBrandService adminBrandService;

    public AdminBrandResource(
            AdminBrandService adminBrandService
    ) {
        this.adminBrandService = adminBrandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {

        return ResponseEntity.ok(
                adminBrandService.getAllBrands()
        );
    }

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(
            @Valid @RequestBody AdminBrandRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        adminBrandService.createBrand(request)
                );
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Long brandId,
            @Valid @RequestBody AdminBrandRequest request
    ) {

        return ResponseEntity.ok(
                adminBrandService.updateBrand(
                        brandId,
                        request
                )
        );
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<ApiResponse> deactivateBrand(
            @PathVariable Long brandId
    ) {

        adminBrandService.deactivateBrand(brandId);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Brand deactivated successfully"
                )
        );
    }
}