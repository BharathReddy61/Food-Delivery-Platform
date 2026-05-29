package com.fooddelivery.controller;

import com.fooddelivery.dto.MenuItemRequestDto;
import com.fooddelivery.dto.MenuItemResponseDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.MenuItemService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final UserRepository userRepository;

    public MenuItemController(
            MenuItemService menuItemService,
            UserRepository userRepository) {
        this.menuItemService = menuItemService;
        this.userRepository = userRepository;
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<MenuItemResponseDto> createMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequestDto requestDto,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MenuItemResponseDto responseDto = menuItemService.createMenuItem(
                restaurantId,
                requestDto,
                user.getId(),
                user.getRole().name());

        return new ResponseEntity<>(
                responseDto,
                HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponseDto>> getMenuByRestaurant(
            @PathVariable Long restaurantId) {

        List<MenuItemResponseDto> items = menuItemService.getMenuByRestaurant(restaurantId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/restaurant/{restaurantId}/available")
    public ResponseEntity<List<MenuItemResponseDto>> getAvailableMenuItems(
            @PathVariable Long restaurantId) {

        List<MenuItemResponseDto> items = menuItemService.getAvailableMenuItems(restaurantId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItemResponseDto>> getMenuItemsByCategory(
            @PathVariable String category) {

        List<MenuItemResponseDto> items = menuItemService.getMenuItemsByCategory(category);

        return ResponseEntity.ok(items);
    }
}