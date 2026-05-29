package com.fooddelivery.controller;

import com.fooddelivery.dto.AdminRestaurantRequestDto;
import com.fooddelivery.dto.RestaurantResponseDto;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.service.RestaurantService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(
            RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /*
     * CREATE RESTAURANT
     */

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(
            @Valid @RequestBody AdminRestaurantRequestDto dto) {

        Restaurant restaurant = restaurantService.createRestaurant(dto);

        return new ResponseEntity<>(
                restaurant,
                HttpStatus.CREATED);
    }

    /*
     * GET ALL RESTAURANTS
     */

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {

        return ResponseEntity.ok(
                restaurantService.getAllRestaurants());
    }

    /*
     * GET SINGLE RESTAURANT
     */

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                restaurantService.getRestaurantById(id));
    }

    /*
     * FILTER BY CITY
     */

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCity(
            @PathVariable String city) {

        return ResponseEntity.ok(
                restaurantService.getRestaurantsByCity(city));
    }

    /*
     * FILTER BY CUISINE
     */

    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisine(
            @PathVariable String cuisine) {

        return ResponseEntity.ok(
                restaurantService.getRestaurantsByCuisine(cuisine));
    }

    /*
     * TOP RATED
     */

    @GetMapping("/top-rated")
    public ResponseEntity<List<Restaurant>> getTopRatedRestaurants() {

        return ResponseEntity.ok(
                restaurantService.getTopRatedRestaurants());
    }

    /*
     * NEARBY RESTAURANTS
     */

    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponseDto>> getNearbyRestaurants(

            @RequestParam Double latitude,

            @RequestParam Double longitude) {

        return ResponseEntity.ok(
                restaurantService.getNearbyRestaurants(
                        latitude,
                        longitude));
    }
}