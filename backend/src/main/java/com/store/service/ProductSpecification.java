package com.store.service;

import com.store.entity.Product;
import com.store.entity.Review;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filter(
            String search,
            Long categoryId,
            Long brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minRating
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.isTrue(root.get("active"))
            );

            if (search != null && !search.isBlank()) {

                String searchValue =
                        "%" + search.trim().toLowerCase() + "%";

                Predicate nameMatch =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")
                                ),
                                searchValue
                        );

                Predicate descriptionMatch =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("description")
                                ),
                                searchValue
                        );

                predicates.add(
                        criteriaBuilder.or(
                                nameMatch,
                                descriptionMatch
                        )
                );
            }

            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                categoryId
                        )
                );
            }

            if (brandId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("brand").get("id"),
                                brandId
                        )
                );
            }

            if (minPrice != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        )
                );
            }

            if (minRating != null) {

                if (minRating < 1 || minRating > 5) {
                    throw new IllegalArgumentException(
                            "Minimum rating must be between 1 and 5"
                    );
                }

                Subquery<Double> ratingSubquery =
                        query.subquery(Double.class);

                var reviewRoot =
                        ratingSubquery.from(Review.class);

                ratingSubquery.select(
                        criteriaBuilder.avg(
                                reviewRoot.get("rating")
                        )
                );

                ratingSubquery.where(
                        criteriaBuilder.equal(
                                reviewRoot
                                        .get("product")
                                        .get("id"),
                                root.get("id")
                        )
                );

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                ratingSubquery,
                                minRating.doubleValue()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}