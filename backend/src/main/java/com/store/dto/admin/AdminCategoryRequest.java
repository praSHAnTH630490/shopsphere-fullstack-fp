package com.store.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCategoryRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 500)
        String imageUrl
) {
}