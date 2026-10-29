package com.store.resource;

import com.store.dto.review.CreateReviewRequest;
import com.store.dto.review.ReviewResponse;
import com.store.dto.review.UpdateReviewRequest;
import com.store.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewResource {

    private final ReviewService reviewService;

    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(productId)
        );
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request
    ) {

        return ResponseEntity.ok(
                reviewService.createReview(
                        authentication.getName(),
                        productId,
                        request
                )
        );
    }

    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {

        return ResponseEntity.ok(
                reviewService.updateReview(
                        authentication.getName(),
                        reviewId,
                        request
                )
        );
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId
    ) {

        reviewService.deleteReview(
                authentication.getName(),
                reviewId
        );

        return ResponseEntity.noContent().build();
    }
}