package com.fooddelivery.service;

import com.fooddelivery.dto.AdminRestaurantRequestDto;
import com.fooddelivery.dto.RestaurantResponseDto;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.RestaurantRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /*
     * CREATE
     */

    public Restaurant createRestaurant(
            AdminRestaurantRequestDto dto) {

        Restaurant restaurant = new Restaurant();

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setCuisineType(dto.getCuisineType());

        restaurant.setAddress(dto.getAddress());
        restaurant.setCity(dto.getCity());

        restaurant.setImageUrl(dto.getImageUrl());

        restaurant.setOwnerId(dto.getOwnerId());

        /*
         * Geo coordinates
         */
        restaurant.setLatitude(dto.getLatitude());
        restaurant.setLongitude(dto.getLongitude());

        /*
         * Delivery radius
         */
        restaurant.setDeliveryRadiusKm(
                dto.getDeliveryRadiusKm() != null
                        ? dto.getDeliveryRadiusKm()
                        : 10.0);

        restaurant.setRating(0.0);

        restaurant.setOpen(true);

        return restaurantRepository.save(restaurant);
    }

    /*
     * UPDATE
     */

    public Restaurant updateRestaurant(
            Long restaurantId,
            AdminRestaurantRequestDto dto,
            Long userId,
            String userRole) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        /*
         * Ownership validation
         */

        if (!userRole.equals("ADMIN") &&
                !userId.equals(restaurant.getOwnerId())) {
            throw new BusinessException(
                    "You do not have permission to manage this restaurant");
        }

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());

        restaurant.setCuisineType(dto.getCuisineType());

        restaurant.setAddress(dto.getAddress());
        restaurant.setCity(dto.getCity());

        restaurant.setImageUrl(dto.getImageUrl());

        /*
         * Geo coordinates
         */

        restaurant.setLatitude(dto.getLatitude());
        restaurant.setLongitude(dto.getLongitude());

        /*
         * Delivery radius
         */

        if (dto.getDeliveryRadiusKm() != null) {

            restaurant.setDeliveryRadiusKm(
                    dto.getDeliveryRadiusKm());
        }

        /*
         * Admin-only ownership reassignment
         */

        if (userRole.equals("ADMIN") &&
                dto.getOwnerId() != null) {

            restaurant.setOwnerId(dto.getOwnerId());
        }

        return restaurantRepository.save(restaurant);
    }

    /*
     * DELETE
     */

    public void deleteRestaurant(
            Long restaurantId,
            Long userId,
            String userRole) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (!userRole.equals("ADMIN") &&
                !userId.equals(restaurant.getOwnerId())) {
            throw new BusinessException(
                    "You do not have permission to delete this restaurant");
        }

        restaurantRepository.delete(restaurant);
    }

    /*
     * GET SINGLE
     */

    public Restaurant getRestaurantById(Long id) {

        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    /*
     * OWNER FILTER
     */

    public List<Restaurant> getRestaurantsByOwnerId(
            Long ownerId) {

        return restaurantRepository.findByOwnerId(ownerId);
    }

    /*
     * ALL RESTAURANTS
     */

    public List<Restaurant> getAllRestaurants() {

        return restaurantRepository.findAll();
    }

    /*
     * CITY FILTER
     */

    public List<Restaurant> getRestaurantsByCity(
            String city) {

        return restaurantRepository.findByCityIgnoreCase(city);
    }

    /*
     * CUISINE FILTER
     */

    public List<Restaurant> getRestaurantsByCuisine(
            String cuisine) {

        return restaurantRepository.findByCuisineType(cuisine);
    }

    /*
     * TOP RATED
     */

    public List<Restaurant> getTopRatedRestaurants() {

        return restaurantRepository
                .findByRatingGreaterThanEqual(4.0);
    }

    /*
     * NEARBY RESTAURANTS
     */

    public List<RestaurantResponseDto> getNearbyRestaurants(
            Double latitude,
            Double longitude) {

        List<Restaurant> restaurants = restaurantRepository.findNearbyRestaurants(
                latitude,
                longitude);

        return restaurants.stream()
                .map(restaurant -> {

                    Double distanceKm = calculateDistance(
                            latitude,
                            longitude,
                            restaurant.getLatitude(),
                            restaurant.getLongitude());

                    return mapToResponseDto(
                            restaurant,
                            distanceKm);

                })
                .collect(Collectors.toList());
    }

    /*
     * DTO MAPPER
     */

    private RestaurantResponseDto mapToResponseDto(
            Restaurant restaurant,
            Double distanceKm) {

        return new RestaurantResponseDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCuisineType(),
                restaurant.getAddress(),
                restaurant.getCity(),
                restaurant.getRating(),
                restaurant.getOpen(),
                restaurant.getImageUrl(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                distanceKm,
                restaurant.getDeliveryRadiusKm());
    }

    /*
     * Haversine Distance Calculator
     */

    private Double calculateDistance(
            Double userLat,
            Double userLon,
            Double restaurantLat,
            Double restaurantLon) {

        if (userLat == null ||
                userLon == null ||
                restaurantLat == null ||
                restaurantLon == null) {
            return null;
        }

        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(restaurantLat - userLat);

        double lonDistance = Math.toRadians(restaurantLon - userLon);

        double a = Math.sin(latDistance / 2)
                * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat))
                        * Math.cos(Math.toRadians(restaurantLat))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}