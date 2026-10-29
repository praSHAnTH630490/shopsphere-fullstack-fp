package com.store.service;

import com.store.dao.CartDAO;
import com.store.dao.CartItemDAO;
import com.store.dao.ProductDAO;
import com.store.dao.UserDAO;
import com.store.dto.cart.AddCartItemRequest;
import com.store.dto.cart.CartResponse;
import com.store.dto.cart.UpdateCartItemRequest;
import com.store.entity.Cart;
import com.store.entity.CartItem;
import com.store.entity.Product;
import com.store.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartDAO cartDAO;

    @Mock
    private CartItemDAO cartItemDAO;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        testUser.setEmail("buyer@store.com");

        testCart = new Cart();
        ReflectionTestUtils.setField(testCart, "id", 10L);
        testCart.setUser(testUser);

        testProduct = new Product();
        ReflectionTestUtils.setField(testProduct, "id", 100L);
        testProduct.setName("Running Shoes");
        testProduct.setPrice(new BigDecimal("2999.00"));
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);
    }

    @Test
    @DisplayName("addItem - should add new item and recalculate cart total")
    void addItem_whenItemNotPresent_shouldAddNewItem() {
        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productDAO.findById(100L)).thenReturn(Optional.of(testProduct));
        when(cartItemDAO.findByCartIdAndProduct(10L, testProduct)).thenReturn(Optional.empty());

        CartItem savedItem = new CartItem();
        ReflectionTestUtils.setField(savedItem, "id", 50L);
        savedItem.setCart(testCart);
        savedItem.setProduct(testProduct);
        savedItem.setQuantity(2);

        when(cartItemDAO.findByCartId(10L)).thenReturn(List.of(savedItem));

        CartResponse response = cartService.addItem("buyer@store.com", new AddCartItemRequest(100L, 2));

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("5998.00"));
        verify(cartItemDAO).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItem - should throw 400 when quantity exceeds available stock")
    void addItem_whenQuantityExceedsStock_shouldThrowException() {
        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productDAO.findById(100L)).thenReturn(Optional.of(testProduct));
        when(cartItemDAO.findByCartIdAndProduct(10L, testProduct)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem("buyer@store.com", new AddCartItemRequest(100L, 15)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Requested quantity exceeds available stock");

        verify(cartItemDAO, never()).save(any());
    }

    @Test
    @DisplayName("updateItem - should update quantity when within stock limits")
    void updateItem_whenQuantityValid_shouldUpdateQuantity() {
        CartItem cartItem = new CartItem();
        ReflectionTestUtils.setField(cartItem, "id", 50L);
        cartItem.setCart(testCart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findById(50L)).thenReturn(Optional.of(cartItem));
        when(cartItemDAO.findByCartId(10L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.updateItem("buyer@store.com", 50L, new UpdateCartItemRequest(4));

        assertThat(response).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(4);
        verify(cartItemDAO).save(cartItem);
    }

    @Test
    @DisplayName("updateItem - should throw 400 when updating beyond stock")
    void updateItem_whenQuantityExceedsStock_shouldThrowException() {
        CartItem cartItem = new CartItem();
        ReflectionTestUtils.setField(cartItem, "id", 50L);
        cartItem.setCart(testCart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findById(50L)).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartService.updateItem("buyer@store.com", 50L, new UpdateCartItemRequest(25)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Requested quantity exceeds available stock");

        verify(cartItemDAO, never()).save(cartItem);
    }

    @Test
    @DisplayName("removeItem - should remove item belonging to user's cart")
    void removeItem_whenItemBelongsToUser_shouldDelete() {
        CartItem cartItem = new CartItem();
        ReflectionTestUtils.setField(cartItem, "id", 50L);
        cartItem.setCart(testCart);
        cartItem.setProduct(testProduct);

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findById(50L)).thenReturn(Optional.of(cartItem));
        when(cartItemDAO.findByCartId(10L)).thenReturn(Collections.emptyList());

        CartResponse response = cartService.removeItem("buyer@store.com", 50L);

        assertThat(response.items()).isEmpty();
        verify(cartItemDAO).delete(cartItem);
    }

    @Test
    @DisplayName("clearCart - should delete all cart items for user")
    void clearCart_shouldDeleteAllItems() {
        CartItem cartItem = new CartItem();
        ReflectionTestUtils.setField(cartItem, "id", 50L);
        cartItem.setCart(testCart);

        when(userDAO.findByEmail("buyer@store.com")).thenReturn(Optional.of(testUser));
        when(cartDAO.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemDAO.findByCartId(10L)).thenReturn(List.of(cartItem));

        cartService.clearCart("buyer@store.com");

        verify(cartItemDAO).deleteAll(List.of(cartItem));
    }
}
