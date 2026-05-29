package com.fooddelivery.dto;

import com.fooddelivery.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {
    private Long orderId;
    private Long restaurantId;
    private Double totalAmount;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> items;
    private List<String> allowedTransitions;

    public OrderResponseDto() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemResponseDto> getItems() { return items; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }

    public List<String> getAllowedTransitions() { return allowedTransitions; }
    public void setAllowedTransitions(List<String> allowedTransitions) { this.allowedTransitions = allowedTransitions; }
}
