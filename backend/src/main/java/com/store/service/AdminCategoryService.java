package com.store.service;

import com.store.dao.CategoryDAO;
import com.store.dto.admin.AdminCategoryRequest;
import com.store.dto.catalog.CategoryResponse;
import com.store.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCategoryService {

    private final CategoryDAO categoryDAO;

    public AdminCategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<CategoryResponse> getAllCategories() {

        return categoryDAO.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(
            AdminCategoryRequest request
    ) {

        String name = request.name().trim();

        if (categoryDAO.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Category name already exists"
            );
        }

        Category category = new Category();

        category.setName(name);
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setActive(true);

        Category savedCategory = categoryDAO.save(category);

        return toResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(
            Long categoryId,
            AdminCategoryRequest request
    ) {

        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found"
                        )
                );

        String requestedName = request.name().trim();

        if (!category.getName().equalsIgnoreCase(requestedName)
                && categoryDAO.existsByNameIgnoreCase(requestedName)) {

            throw new IllegalArgumentException(
                    "Category name already exists"
            );
        }

        category.setName(requestedName);
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());

        Category savedCategory = categoryDAO.save(category);

        return toResponse(savedCategory);
    }

    @Transactional
    public void deactivateCategory(Long categoryId) {

        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found"
                        )
                );

        category.setActive(false);

        categoryDAO.save(category);
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