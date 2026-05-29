package com.fooddelivery.controller;

import com.fooddelivery.dto.AddToCartRequestDto;
import com.fooddelivery.dto.CartResponseDto;
import com.fooddelivery.dto.UpdateCartItemQuantityDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private Long getAuthenticatedUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        return user.getId();
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addToCart(@Valid @RequestBody AddToCartRequestDto dto) {
        Long userId = getAuthenticatedUserId();
        CartResponseDto response = cartService.addToCart(userId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart() {
        Long userId = getAuthenticatedUserId();
        CartResponseDto response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/item/{menuItemId}")
    public ResponseEntity<CartResponseDto> removeCartItem(@PathVariable Long menuItemId) {
        Long userId = getAuthenticatedUserId();
        CartResponseDto response = cartService.removeCartItem(userId, menuItemId);
        return ResponseEntity.ok(response);
    }

    // Dedicated quantity replacement endpoint — NOT additive
    @PatchMapping("/items/{menuItemId}/quantity")
    public ResponseEntity<CartResponseDto> updateCartItemQuantity(
            @PathVariable Long menuItemId,
            @Valid @RequestBody UpdateCartItemQuantityDto dto
    ) {
        Long userId = getAuthenticatedUserId();
        CartResponseDto response = cartService.updateCartItemQuantity(userId, menuItemId, dto.getQuantity());
        return ResponseEntity.ok(response);
    }
}
