package com.fooddelivery.dto;

public class CartItemResponseDto {
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double itemPrice;
    private Double itemTotal;

    public CartItemResponseDto() {
    }

    public CartItemResponseDto(Long menuItemId, String menuItemName, Integer quantity, Double itemPrice, Double itemTotal) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.itemTotal = itemTotal;
    }

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getItemPrice() { return itemPrice; }
    public void setItemPrice(Double itemPrice) { this.itemPrice = itemPrice; }

    public Double getItemTotal() { return itemTotal; }
    public void setItemTotal(Double itemTotal) { this.itemTotal = itemTotal; }
}
