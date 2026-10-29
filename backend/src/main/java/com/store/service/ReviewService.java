package com.store.service;

import com.store.dao.OrderDAO;
import com.store.dao.ProductDAO;
import com.store.dao.ReviewDAO;
import com.store.dao.UserDAO;
import com.store.dto.review.CreateReviewRequest;
import com.store.dto.review.ReviewResponse;
import com.store.dto.review.UpdateReviewRequest;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Product;
import com.store.entity.Review;
import com.store.entity.User;
import com.store.entity.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewService(
            UserDAO userDAO,
            ProductDAO productDAO,
            ReviewDAO reviewDAO,
            OrderDAO orderDAO
    ) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    @Transactional
    public ReviewResponse createReview(
            String email,
            Long productId,
            CreateReviewRequest request
    ) {

        User user = getUser(email);

        Product product = productDAO
                .findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Product is no longer available"
            );
        }

        if (reviewDAO.existsByUserAndProduct(user, product)) {
            throw new IllegalArgumentException(
                    "You have already reviewed this product"
            );
        }

        if (!hasPurchasedProduct(user, product)) {
            throw new IllegalArgumentException(
                    "You can review a product only after purchasing it"
            );
        }

        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());

        Review savedReview = reviewDAO.save(review);

        return toResponse(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(
            String email,
            Long reviewId,
            UpdateReviewRequest request
    ) {

        User user = getUser(email);

        Review review = reviewDAO
                .findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Review not found"
                        )
                );

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to update this review"
            );
        }

        review.setRating(request.rating());
        review.setComment(request.comment());

        Review savedReview = reviewDAO.save(review);

        return toResponse(savedReview);
    }

    @Transactional
    public void deleteReview(
            String email,
            Long reviewId
    ) {

        User user = getUser(email);

        Review review = reviewDAO
                .findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Review not found"
                        )
                );

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to delete this review"
            );
        }

        reviewDAO.delete(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(
            Long productId
    ) {

        Product product = productDAO
                .findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        return reviewDAO
                .findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private boolean hasPurchasedProduct(
            User user,
            Product product
    ) {

        List<Order> orders =
                orderDAO.findByUserOrderByCreatedAtDesc(user);

        for (Order order : orders) {

            if (order.getStatus() != OrderStatus.DELIVERED) {
                continue;
            }

            for (OrderItem orderItem : order.getItems()) {

                if (orderItem.getProduct()
                        .getId()
                        .equals(product.getId())) {

                    return true;
                }
            }
        }

        return false;
    }

    private User getUser(String email) {

        return userDAO
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private ReviewResponse toResponse(
            Review review
    ) {

        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}