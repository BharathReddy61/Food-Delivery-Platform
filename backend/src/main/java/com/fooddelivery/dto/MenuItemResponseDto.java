package com.fooddelivery.dto;

public class MenuItemResponseDto {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private Boolean available;

    private String category;

    /*
     * Food image URL
     */
    private String imageUrl;

    /*
     * Restaurant metadata
     */
    private Long restaurantId;

    private String restaurantName;

    public MenuItemResponseDto() {
    }

    public MenuItemResponseDto(
            Long id,
            String name,
            String description,
            Double price,
            Boolean available,
            String category,
            String imageUrl,
            Long restaurantId,
            String restaurantName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.category = category;
        this.imageUrl = imageUrl;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    /*
     * ID
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
     * Basic Info
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * Description
     */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Pricing
     */

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /*
     * Availability
     */

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /*
     * Category
     */

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /*
     * Image
     */

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /*
     * Restaurant Info
     */

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}