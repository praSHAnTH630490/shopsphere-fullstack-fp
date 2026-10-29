package com.store.dto.admin;

import java.math.BigDecimal;

public record AdminDashboardResponse(

        long totalUsers,
        long totalProducts,
        long activeProducts,
        long totalCategories,
        long totalBrands,
        long totalOrders,
        long placedOrders,
        long confirmedOrders,
        long packedOrders,
        long outForDeliveryOrders,
        long deliveredOrders,
        long cancelledOrders,
        BigDecimal totalRevenue

) {
}