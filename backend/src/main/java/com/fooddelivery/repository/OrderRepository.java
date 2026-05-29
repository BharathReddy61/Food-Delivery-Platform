package com.fooddelivery.repository;

import com.fooddelivery.entity.Order;
import com.fooddelivery.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByRestaurantIdIn(List<Long> restaurantIds);
    List<Order> findByDeliveryPartnerId(Long deliveryPartnerId);
    
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.deliveryPartnerId IS NULL AND o.status IN (:statuses)")
    List<Order> findAvailableOrders(List<OrderStatus> statuses);
}
