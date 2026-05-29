package com.fooddelivery.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String cuisineType;

    private String address;

    private String city;

    private Double rating;

    private Boolean isOpen;

    private Long ownerId;

    /*
     * Restaurant thumbnail / banner image
     */
    private String imageUrl;

    /*
     * Geo-location coordinates
     */
    private Double latitude;

    private Double longitude;

    /*
     * Delivery radius in kilometers
     */
    private Double deliveryRadiusKm = 10.0;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("restaurant")
    private List<MenuItem> menuItems = new ArrayList<>();

    public Restaurant() {
    }

    public Restaurant(
            Long id,
            String name,
            String description,
            String cuisineType,
            String address,
            String city,
            Double rating,
            Boolean isOpen,
            Long ownerId,
            String imageUrl,
            Double latitude,
            Double longitude,
            Double deliveryRadiusKm) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.address = address;
        this.city = city;
        this.rating = rating;
        this.isOpen = isOpen;
        this.ownerId = ownerId;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /*
     * Address & City
     */

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /*
     * Ratings & Status
     */

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    /*
     * Ownership
     */

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /*
     * Images
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
     * Delivery Radius
     */

    public Double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(Double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

    /*
     * Menu Items
     */

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}