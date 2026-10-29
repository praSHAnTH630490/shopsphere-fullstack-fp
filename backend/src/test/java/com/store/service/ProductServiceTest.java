package com.store.service;

import com.store.dao.ProductDAO;
import com.store.dto.catalog.ProductResponse;
import com.store.entity.Brand;
import com.store.entity.Category;
import com.store.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("searchProducts - searches active products with pagination and mapping")
    void searchProducts_whenValidParams_shouldReturnPage() {
        Category cat = new Category();
        ReflectionTestUtils.setField(cat, "id", 1L);
        cat.setName("Footwear");

        Brand brand = new Brand();
        ReflectionTestUtils.setField(brand, "id", 2L);
        brand.setName("Nike");

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 10L);
        product.setName("Air Max 90");
        product.setCategory(cat);
        product.setBrand(brand);
        product.setPrice(new BigDecimal("9999.00"));
        product.setStockQuantity(15);
        product.setActive(true);

        Page<Product> pageResult = new PageImpl<>(List.of(product));
        when(productDAO.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);

        Page<ProductResponse> response = productService.searchProducts(
                0,
                12,
                "Air",
                1L,
                2L,
                BigDecimal.ZERO,
                new BigDecimal("15000.00"),
                4,
                "price_asc"
        );

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        ProductResponse dto = response.getContent().get(0);
        assertThat(dto.name()).isEqualTo("Air Max 90");
        assertThat(dto.categoryName()).isEqualTo("Footwear");
        assertThat(dto.brandName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("searchProducts - throws 400 when page number is negative")
    void searchProducts_whenPageNegative_shouldThrowException() {
        assertThatThrownBy(() -> productService.searchProducts(
                -1, 10, null, null, null, null, null, null, null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page number cannot be negative");
    }

    @Test
    @DisplayName("searchProducts - throws 400 when page size is invalid")
    void searchProducts_whenPageSizeInvalid_shouldThrowException() {
        assertThatThrownBy(() -> productService.searchProducts(
                0, 0, null, null, null, null, null, null, null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be between 1 and 100");
    }
}
