package com.store.resource;

import com.store.dto.catalog.BrandResponse;
import com.store.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandResource {

    private final BrandService brandService;

    public BrandResource(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getBrands() {
        return ResponseEntity.ok(
                brandService.getActiveBrands()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrand(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                brandService.getBrand(id)
        );
    }
}