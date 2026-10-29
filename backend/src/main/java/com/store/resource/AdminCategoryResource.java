package com.store.resource;

import com.store.dto.ApiResponse;
import com.store.dto.admin.AdminCategoryRequest;
import com.store.dto.catalog.CategoryResponse;
import com.store.service.AdminCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryResource {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryResource(
            AdminCategoryService adminCategoryService
    ) {
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                adminCategoryService.getAllCategories()
        );
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody AdminCategoryRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        adminCategoryService.createCategory(request)
                );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody AdminCategoryRequest request
    ) {

        return ResponseEntity.ok(
                adminCategoryService.updateCategory(
                        categoryId,
                        request
                )
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deactivateCategory(
            @PathVariable Long categoryId
    ) {

        adminCategoryService.deactivateCategory(categoryId);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Category deactivated successfully"
                )
        );
    }
}