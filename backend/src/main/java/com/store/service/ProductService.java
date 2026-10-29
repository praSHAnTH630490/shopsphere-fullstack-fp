package com.store.service;

import com.store.dao.ProductDAO;
import com.store.dto.catalog.ProductResponse;
import com.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {

        return productDAO.findAll(
                        ProductSpecification.filter(
        null,
        null,
        null,
        null,
        null,
        null
),
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
        int page,
        int size,
        String search,
        Long categoryId,
        Long brandId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minRating,
        String sort
) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        Sort sorting = createSort(sort);

        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        return productDAO.findAll(
                        ProductSpecification.filter(
        search,
        categoryId,
        brandId,
        minPrice,
        maxPrice,
        minRating
),
                        pageable
                )
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {

        Product product = productDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Product is not active"
            );
        }

        return toResponse(product);
    }

    private Sort createSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        return switch (sort.toLowerCase()) {

            case "price_asc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "price"
                    );

            case "price_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "price"
                    );

            case "name_asc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "name"
                    );

            case "name_desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "name"
                    );

            case "newest" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    );

            case "oldest" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "createdAt"
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Invalid sort option"
                    );
        };
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand().getId(),
                product.getBrand().getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getSku(),
                product.isActive(),
                product.getCreatedAt()
        );
    }
}