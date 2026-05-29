package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequestDto {
    @NotBlank(message = "Delivery address is required")
    @jakarta.validation.constraints.Size(max = 500, message = "Address is too long")
    private String deliveryAddress;

    public CreateOrderRequestDto() {}

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
