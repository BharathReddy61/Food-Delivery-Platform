package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MenuItemRequestDto {
    
    @NotBlank(message = "Name cannot be blank")
    private String name;
    
    @NotBlank(message = "Description cannot be blank")
    private String description;
    
    @Positive(message = "Price must be positive")
    private Double price;
    
    @NotBlank(message = "Category cannot be blank")
    private String category;
    
    private String imageUrl;

    @NotNull(message = "Availability status is required")
    private Boolean available;

    public MenuItemRequestDto() {
    }

    public MenuItemRequestDto(String name, String description, Double price, String category, String imageUrl, Boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
