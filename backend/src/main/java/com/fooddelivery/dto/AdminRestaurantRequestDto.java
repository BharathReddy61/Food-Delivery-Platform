package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminRestaurantRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    /*
     * Restaurant image
     */
    private String imageUrl;

    /*
     * Geo coordinates
     */

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    /*
     * Delivery radius in KM
     */
    private Double deliveryRadiusKm = 10.0;

    /*
     * Ownership
     */

    @NotNull(message = "Owner ID is required for restaurant creation")
    private Long ownerId;

    public AdminRestaurantRequestDto() {
    }

    /*
     * Name
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
     * Cuisine
     */

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /*
     * Address
     */

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /*
     * City
     */

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
     * Latitude
     */

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /*
     * Longitude
     */

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /*
     * Delivery Radius
     */

    public Double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(Double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

    /*
     * Owner
     */

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}