package com.fooddelivery.controller;

import com.fooddelivery.dto.MenuItemRequestDto;
import com.fooddelivery.dto.MenuItemResponseDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu/manage")
public class AdminMenuController {

    private final MenuItemService menuItemService;
    private final UserRepository userRepository;

    public AdminMenuController(MenuItemService menuItemService, UserRepository userRepository) {
        this.menuItemService = menuItemService;
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

    @PostMapping("/{restaurantId}")
    public ResponseEntity<MenuItemResponseDto> createMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequestDto dto
    ) {
        User user = getAuthenticatedUser();
        MenuItemResponseDto response = menuItemService.createMenuItem(restaurantId, dto, user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItemResponseDto> updateMenuItem(
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemRequestDto dto
    ) {
        User user = getAuthenticatedUser();
        MenuItemResponseDto response = menuItemService.updateMenuItem(itemId, dto, user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long itemId) {
        User user = getAuthenticatedUser();
        menuItemService.deleteMenuItem(itemId, user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok("Menu item deleted successfully");
    }
}
