package com.store.service;

import com.store.dao.ProductDAO;
import com.store.dao.UserDAO;
import com.store.dao.WishlistDAO;
import com.store.dao.WishlistItemDAO;
import com.store.dto.wishlist.WishlistItemResponse;
import com.store.dto.wishlist.WishlistResponse;
import com.store.entity.Product;
import com.store.entity.User;
import com.store.entity.Wishlist;
import com.store.entity.WishlistItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistDAO wishlistDAO;
    private final WishlistItemDAO wishlistItemDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    public WishlistService(
            WishlistDAO wishlistDAO,
            WishlistItemDAO wishlistItemDAO,
            ProductDAO productDAO,
            UserDAO userDAO
    ) {
        this.wishlistDAO = wishlistDAO;
        this.wishlistItemDAO = wishlistItemDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public WishlistResponse getWishlist(String email) {

        User user = getUser(email);
        Wishlist wishlist = getOrCreateWishlist(user);

        return toResponse(wishlist);
    }

    @Transactional
    public WishlistResponse addProduct(
            String email,
            Long productId
    ) {

        User user = getUser(email);
        Wishlist wishlist = getOrCreateWishlist(user);

        Product product = productDAO.findById(productId)
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

        boolean alreadyExists =
                wishlistItemDAO.existsByWishlistIdAndProduct(
                        wishlist.getId(),
                        product
                );

        if (!alreadyExists) {

            WishlistItem item = new WishlistItem();

            item.setWishlist(wishlist);
            item.setProduct(product);

            wishlistItemDAO.save(item);
        }

        return toResponse(wishlist);
    }

    @Transactional
    public WishlistResponse removeProduct(
            String email,
            Long productId
    ) {

        User user = getUser(email);
        Wishlist wishlist = getOrCreateWishlist(user);

        Product product = productDAO.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        WishlistItem item =
                wishlistItemDAO
                        .findByWishlistIdAndProduct(
                                wishlist.getId(),
                                product
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product is not in wishlist"
                                )
                        );

        wishlistItemDAO.delete(item);

        return toResponse(wishlist);
    }

    private User getUser(String email) {

        return userDAO.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private Wishlist getOrCreateWishlist(User user) {

        return wishlistDAO.findByUser(user)
                .orElseGet(() -> {

                    Wishlist wishlist = new Wishlist();

                    wishlist.setUser(user);

                    return wishlistDAO.save(wishlist);
                });
    }

    private WishlistResponse toResponse(
            Wishlist wishlist
    ) {

        List<WishlistItemResponse> items =
                wishlistItemDAO.findAll()
                        .stream()
                        .filter(item ->
                                item.getWishlist()
                                        .getId()
                                        .equals(wishlist.getId())
                        )
                        .map(this::toItemResponse)
                        .toList();

        return new WishlistResponse(
                wishlist.getId(),
                items
        );
    }

    private WishlistItemResponse toItemResponse(
            WishlistItem item
    ) {

        Product product = item.getProduct();

        return new WishlistItemResponse(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                product.isActive()
        );
    }
}