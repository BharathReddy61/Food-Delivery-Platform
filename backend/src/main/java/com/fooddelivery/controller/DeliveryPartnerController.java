package com.fooddelivery.controller;

import com.fooddelivery.dto.OrderResponseDto;
import com.fooddelivery.dto.UpdateOrderStatusDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class DeliveryPartnerController {

    private final DeliveryService deliveryService;
    private final UserRepository userRepository;

    public DeliveryPartnerController(DeliveryService deliveryService, UserRepository userRepository) {
        this.deliveryService = deliveryService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    /**
     * Get orders ready for assignment.
     */
    @GetMapping("/available")
    public ResponseEntity<List<OrderResponseDto>> getAvailableOrders() {
        return ResponseEntity.ok(deliveryService.getAvailableOrders());
    }

    /**
     * Get orders assigned to the authenticated partner.
     */
    @GetMapping("/my-deliveries")
    public ResponseEntity<List<OrderResponseDto>> getMyDeliveries() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(deliveryService.getPartnerDeliveries(user.getId()));
    }

    /**
     * Claim/Assign an order to the authenticated partner.
     */
    @PostMapping("/assign/{orderId}")
    public ResponseEntity<OrderResponseDto> assignOrder(@PathVariable Long orderId) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(deliveryService.assignOrder(orderId, user.getId()));
    }

    /**
     * Update delivery status (PICKED_UP, ARRIVING, DELIVERED).
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateDeliveryStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusDto dto
    ) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(orderId, dto.getStatus(), user.getId()));
    }
}
