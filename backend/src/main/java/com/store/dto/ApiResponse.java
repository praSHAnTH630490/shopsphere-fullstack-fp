package com.store.dto;

public record ApiResponse(
        boolean success,
        String message
) {
}