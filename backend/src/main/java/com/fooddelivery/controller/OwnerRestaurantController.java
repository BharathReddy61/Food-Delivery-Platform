package com.fooddelivery.controller;

import com.fooddelivery.dto.AdminRestaurantRequestDto;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/restaurants")
public class OwnerRestaurantController {

    private final RestaurantService restaurantService;
    private final UserRepository userRepository;

    public OwnerRestaurantController(RestaurantService restaurantService, UserRepository userRepository) {
        this.restaurantService = restaurantService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private String getAuthenticatedUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority().replace("ROLE_", "");
    }

    @GetMapping
    public ResponseEntity<java.util.List<com.fooddelivery.entity.Restaurant>> getMyRestaurants() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwnerId(user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody AdminRestaurantRequestDto dto
    ) {
        User user = getAuthenticatedUser();
        // OwnerId must be preserved to their own ID. Enforce DTO logic.
        dto.setOwnerId(user.getId());
        Restaurant restaurant = restaurantService.updateRestaurant(id, dto, user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok(restaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        restaurantService.deleteRestaurant(id, user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok("Restaurant deleted successfully");
    }
}
