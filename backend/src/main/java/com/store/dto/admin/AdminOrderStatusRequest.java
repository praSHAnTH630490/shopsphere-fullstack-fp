package com.store.dto.admin;

import com.store.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record AdminOrderStatusRequest(

        @NotNull
        OrderStatus status

) {
}