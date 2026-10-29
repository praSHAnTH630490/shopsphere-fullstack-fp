package com.store.service;

import com.store.dao.CartDAO;
import com.store.dao.CartItemDAO;
import com.store.dao.ProductDAO;
import com.store.dao.UserDAO;
import com.store.dto.cart.AddCartItemRequest;
import com.store.dto.cart.CartItemResponse;
import com.store.dto.cart.CartResponse;
import com.store.dto.cart.UpdateCartItemRequest;
import com.store.entity.Cart;
import com.store.entity.CartItem;
import com.store.entity.Product;
import com.store.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartDAO cartDAO;
    private final CartItemDAO cartItemDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    public CartService(
            CartDAO cartDAO,
            CartItemDAO cartItemDAO,
            ProductDAO productDAO,
            UserDAO userDAO
    ) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public CartResponse getCart(String email) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(
            String email,
            AddCartItemRequest request
    ) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        Product product = productDAO.findById(request.productId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        validateProduct(product);

        CartItem cartItem =
                cartItemDAO.findByCartIdAndProduct(
                        cart.getId(),
                        product
                ).orElse(null);

        int newQuantity;

        if (cartItem == null) {

            newQuantity = request.quantity();

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);

        } else {

            newQuantity =
                    cartItem.getQuantity() + request.quantity();
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(newQuantity);

        cartItemDAO.save(cartItem);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(
            String email,
            Long cartItemId,
            UpdateCartItemRequest request
    ) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemDAO.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found"
                        )
                );

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException(
                    "Cart item does not belong to your cart"
            );
        }

        Product product = cartItem.getProduct();

        validateProduct(product);

        if (request.quantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(request.quantity());

        cartItemDAO.save(cartItem);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(
            String email,
            Long cartItemId
    ) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemDAO.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found"
                        )
                );

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException(
                    "Cart item does not belong to your cart"
            );
        }

        cartItemDAO.delete(cartItem);

        return toResponse(cart);
    }

    @Transactional
    public void clearCart(String email) {

        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        List<CartItem> items = cartItemDAO.findByCartId(cart.getId());

        cartItemDAO.deleteAll(items);
    }

    private User getUser(String email) {

        return userDAO.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private Cart getOrCreateCart(User user) {

        return cartDAO.findByUser(user)
                .orElseGet(() -> {

                    Cart cart = new Cart();
                    cart.setUser(user);

                    return cartDAO.save(cart);
                });
    }

    private void validateProduct(Product product) {

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Product is not active"
            );
        }

        if (product.getStockQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Product is out of stock"
            );
        }
    }

    private CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cartItemDAO.findByCartId(cart.getId())
                .stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal totalAmount =
                items.stream()
                        .map(CartItemResponse::subtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new CartResponse(
                cart.getId(),
                items,
                totalAmount
        );
    }

    private CartItemResponse toItemResponse(
            CartItem item
    ) {

        Product product = item.getProduct();

        BigDecimal subtotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()
                                )
                        );

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                item.getQuantity(),
                subtotal
        );
    }
}
