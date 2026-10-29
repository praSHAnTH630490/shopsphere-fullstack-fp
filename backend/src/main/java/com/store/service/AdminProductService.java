package com.store.service;

import com.store.dao.BrandDAO;
import com.store.dao.CategoryDAO;
import com.store.dao.ProductDAO;
import com.store.dto.admin.AdminProductRequest;
import com.store.dto.catalog.ProductResponse;
import com.store.entity.Brand;
import com.store.entity.Category;
import com.store.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProductService {

    private final ProductDAO productDAO;
    private final BrandDAO brandDAO;
    private final CategoryDAO categoryDAO;

    public AdminProductService(
            ProductDAO productDAO,
            BrandDAO brandDAO,
            CategoryDAO categoryDAO
    ) {
        this.productDAO = productDAO;
        this.brandDAO = brandDAO;
        this.categoryDAO = categoryDAO;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productDAO.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(
            AdminProductRequest request
    ) {

        if (productDAO.existsBySku(request.sku())) {
            throw new IllegalArgumentException(
                    "SKU already exists"
            );
        }

        Brand brand = brandDAO
                .findById(request.brandId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Brand not found"
                        )
                );

        Category category = categoryDAO
                .findById(request.categoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found"
                        )
                );

        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());
        product.setSku(request.sku().trim());
        product.setActive(true);

        Product savedProduct = productDAO.save(product);

        return toResponse(savedProduct);
    }

    @Transactional
public ProductResponse updateProduct(
        Long productId,
        AdminProductRequest request
) {
    Product product = productDAO.findById(productId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Product not found"));

    String requestedSku = request.sku().trim();

    // Check whether this SKU belongs to another product
    Product skuOwner = productDAO.findBySku(requestedSku)
            .orElse(null);

    if (skuOwner != null
            && !skuOwner.getId().equals(product.getId())) {
        throw new IllegalArgumentException("SKU already exists");
    }

    Brand brand = brandDAO.findById(request.brandId())
            .orElseThrow(() ->
                    new IllegalArgumentException("Brand not found"));

    Category category = categoryDAO.findById(request.categoryId())
            .orElseThrow(() ->
                    new IllegalArgumentException("Category not found"));

    product.setName(request.name().trim());
    product.setDescription(request.description());
    product.setBrand(brand);
    product.setCategory(category);
    product.setPrice(request.price());
    product.setStockQuantity(request.stockQuantity());
    product.setImageUrl(request.imageUrl());
    product.setSku(requestedSku);

    Product savedProduct = productDAO.save(product);

    return toResponse(savedProduct);
}

    @Transactional
    public void deactivateProduct(Long productId) {

        Product product = productDAO
                .findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        product.setActive(false);

        productDAO.save(product);
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