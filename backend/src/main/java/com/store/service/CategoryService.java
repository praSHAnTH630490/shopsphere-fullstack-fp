package com.store.service;

import com.store.dao.CategoryDAO;
import com.store.dto.catalog.CategoryResponse;
import com.store.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {

        return categoryDAO.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {

        Category category = categoryDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found")
                );

        if (!category.isActive()) {
            throw new IllegalArgumentException("Category is not active");
        }

        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                category.isActive(),
                category.getCreatedAt()
        );
    }
}