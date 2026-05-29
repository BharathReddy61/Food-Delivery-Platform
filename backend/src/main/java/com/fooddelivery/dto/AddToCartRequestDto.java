package com.fooddelivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AddToCartRequestDto {
    
    @NotNull(message = "Menu item ID cannot be null")
    private Long menuItemId;
    
    @Positive(message = "Quantity must be at least 1")
    private Integer quantity;

    public AddToCartRequestDto() {
    }

    public AddToCartRequestDto(Long menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
