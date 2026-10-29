package com.store.service;

import com.store.dao.BrandDAO;
import com.store.dao.CategoryDAO;
import com.store.dao.OrderDAO;
import com.store.dao.ProductDAO;
import com.store.dao.UserDAO;
import com.store.dto.admin.AdminDashboardResponse;
import com.store.entity.Order;
import com.store.entity.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminDashboardService {

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final BrandDAO brandDAO;
    private final OrderDAO orderDAO;

    public AdminDashboardService(
            UserDAO userDAO,
            ProductDAO productDAO,
            CategoryDAO categoryDAO,
            BrandDAO brandDAO,
            OrderDAO orderDAO
    ) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.brandDAO = brandDAO;
        this.orderDAO = orderDAO;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        long totalUsers = userDAO.count();

        long totalProducts = productDAO.count();

        long activeProducts = productDAO.findAll()
                .stream()
                .filter(product -> product.isActive())
                .count();

        long totalCategories = categoryDAO.count();

        long totalBrands = brandDAO.count();

        List<Order> orders = orderDAO.findAll();

        long totalOrders = orders.size();

        long placedOrders = countByStatus(
                orders,
                OrderStatus.PLACED
        );

        long confirmedOrders = countByStatus(
                orders,
                OrderStatus.CONFIRMED
        );

        long packedOrders = countByStatus(
                orders,
                OrderStatus.PACKED
        );

        long outForDeliveryOrders = countByStatus(
                orders,
                OrderStatus.OUT_FOR_DELIVERY
        );

        long deliveredOrders = countByStatus(
                orders,
                OrderStatus.DELIVERED
        );

        long cancelledOrders = countByStatus(
                orders,
                OrderStatus.CANCELLED
        );

        BigDecimal totalRevenue = orders.stream()
                .filter(order ->
                        order.getStatus() == OrderStatus.DELIVERED
                )
                .map(Order::getTotalAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return new AdminDashboardResponse(
                totalUsers,
                totalProducts,
                activeProducts,
                totalCategories,
                totalBrands,
                totalOrders,
                placedOrders,
                confirmedOrders,
                packedOrders,
                outForDeliveryOrders,
                deliveredOrders,
                cancelledOrders,
                totalRevenue
        );
    }

    private long countByStatus(
            List<Order> orders,
            OrderStatus status
    ) {

        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .count();
    }
}