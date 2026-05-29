package com.fooddelivery.dto;

import com.fooddelivery.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusDto {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public UpdateOrderStatusDto() {}

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
