package com.fooddelivery.repository;

import com.fooddelivery.entity.Restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    /*
     * Basic Filters
     */

    List<Restaurant> findByCity(String city);

    List<Restaurant> findByCuisineType(String cuisineType);

    List<Restaurant> findByIsOpen(Boolean isOpen);

    List<Restaurant> findByRatingGreaterThanEqual(Double rating);

    List<Restaurant> findByOwnerId(Long ownerId);

    /*
     * Search by city ignoring case
     */

    List<Restaurant> findByCityIgnoreCase(String city);

    /*
     * Nearby restaurant search
     * Uses Haversine formula
     */

    @Query(value = """
            SELECT *
            FROM restaurants r
            WHERE
            (
                6371 *
                acos(
                    cos(radians(:latitude)) *
                    cos(radians(r.latitude)) *
                    cos(radians(r.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) *
                    sin(radians(r.latitude))
                )
            ) <= r.delivery_radius_km
            ORDER BY
            (
                6371 *
                acos(
                    cos(radians(:latitude)) *
                    cos(radians(r.latitude)) *
                    cos(radians(r.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) *
                    sin(radians(r.latitude))
                )
            )
            """, nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude);
}