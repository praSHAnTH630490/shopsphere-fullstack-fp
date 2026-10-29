package com.store.resource;

import com.store.dto.order.CreateOrderRequest;
import com.store.dto.order.OrderResponse;
import com.store.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return ResponseEntity.ok(
                orderService.createOrder(
                        authentication.getName(),
                        request
                )
        );
    }
    @GetMapping
public ResponseEntity<List<OrderResponse>> getOrders(
        Authentication authentication
) {

    return ResponseEntity.ok(
            orderService.getUserOrders(
                    authentication.getName()
            )
    );
}
    @GetMapping("/{orderId}")
public ResponseEntity<OrderResponse> getOrder(
        Authentication authentication,
        @PathVariable Long orderId
) {

    return ResponseEntity.ok(
            orderService.getOrder(
                    authentication.getName(),
                    orderId
            )
    );
}
    
    @PostMapping("/{orderId}/cancel")
public ResponseEntity<OrderResponse> cancelOrder(
        Authentication authentication,
        @PathVariable Long orderId
) {

    return ResponseEntity.ok(
            orderService.cancelOrder(
                    authentication.getName(),
                    orderId
            )
    );
}
    
}