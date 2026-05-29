package com.fooddelivery.dto;

import com.fooddelivery.entity.Restaurant;
import java.util.List;

public class OwnerDashboardDto {
    private long activeOrderCount;
    private long completedOrderCount;
    private long cancelledOrderCount;
    private double totalRevenue;
    private List<OrderResponseDto> recentOrders;
    private List<Restaurant> restaurants;

    public OwnerDashboardDto() {}

    public long getActiveOrderCount() { return activeOrderCount; }
    public void setActiveOrderCount(long activeOrderCount) { this.activeOrderCount = activeOrderCount; }

    public long getCompletedOrderCount() { return completedOrderCount; }
    public void setCompletedOrderCount(long completedOrderCount) { this.completedOrderCount = completedOrderCount; }

    public long getCancelledOrderCount() { return cancelledOrderCount; }
    public void setCancelledOrderCount(long cancelledOrderCount) { this.cancelledOrderCount = cancelledOrderCount; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public List<OrderResponseDto> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderResponseDto> recentOrders) { this.recentOrders = recentOrders; }

    public List<Restaurant> getRestaurants() { return restaurants; }
    public void setRestaurants(List<Restaurant> restaurants) { this.restaurants = restaurants; }
}
