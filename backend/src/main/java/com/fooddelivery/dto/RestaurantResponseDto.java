package com.fooddelivery.dto;

public class RestaurantResponseDto {

    private Long id;

    private String name;

    private String description;

    private String cuisineType;

    private String address;

    private String city;

    private Double rating;

    private Boolean isOpen;

    private String imageUrl;

    /*
     * Geo coordinates
     */
    private Double latitude;

    private Double longitude;

    /*
     * Nearby distance from user
     */
    private Double distanceKm;

    /*
     * Delivery support radius
     */
    private Double deliveryRadiusKm;

    public RestaurantResponseDto() {
    }

    public RestaurantResponseDto(
            Long id,
            String name,
            String description,
            String cuisineType,
            String address,
            String city,
            Double rating,
            Boolean isOpen,
            String imageUrl,
            Double latitude,
            Double longitude,
            Double distanceKm,
            Double deliveryRadiusKm) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.address = address;
        this.city = city;
        this.rating = rating;
        this.isOpen = isOpen;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceKm = distanceKm;
        this.deliveryRadiusKm = deliveryRadiusKm;
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
     * Ratings
     */

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    /*
     * Open Status
     */

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
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
     * Geo Coordinates
     */

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /*
     * Distance
     */

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
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
}