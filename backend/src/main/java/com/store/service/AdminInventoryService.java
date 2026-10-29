package com.store.service;

import com.store.dao.ProductDAO;
import com.store.dto.admin.AdminInventoryRequest;
import com.store.dto.admin.AdminInventoryResponse;
import com.store.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminInventoryService {

    private final ProductDAO productDAO;

    public AdminInventoryService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<AdminInventoryResponse> getInventory() {

        return productDAO.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdminInventoryResponse updateStock(
            Long productId,
            AdminInventoryRequest request
    ) {

        Product product = productDAO.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        product.setStockQuantity(request.stockQuantity());

        Product savedProduct = productDAO.save(product);

        return toResponse(savedProduct);
    }

    private AdminInventoryResponse toResponse(Product product) {

        return new AdminInventoryResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getStockQuantity(),
                product.isActive()
        );
    }
}