package com.fooddelivery.dto;

import java.util.List;

public class CartResponseDto {
    private Long cartId;
    private Double totalAmount;
    private List<CartItemResponseDto> items;

    public CartResponseDto() {
    }

    public CartResponseDto(Long cartId, Double totalAmount, List<CartItemResponseDto> items) {
        this.cartId = cartId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<CartItemResponseDto> getItems() { return items; }
    public void setItems(List<CartItemResponseDto> items) { this.items = items; }
}
