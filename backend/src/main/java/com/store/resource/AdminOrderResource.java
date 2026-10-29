package com.store.resource;

import com.store.dto.admin.AdminOrderStatusRequest;
import com.store.dto.order.OrderResponse;
import com.store.service.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderResource {

    private final AdminOrderService adminOrderService;

    public AdminOrderResource(
            AdminOrderService adminOrderService
    ) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                adminOrderService.getAllOrders()
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                adminOrderService.getOrder(orderId)
        );
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody AdminOrderStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminOrderService.updateOrderStatus(
                        orderId,
                        request
                )
        );
    }
}