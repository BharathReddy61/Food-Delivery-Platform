package com.fooddelivery.controller;

import com.fooddelivery.dto.OrderResponseDto;
import com.fooddelivery.dto.UpdateOrderStatusDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public AdminOrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
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

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusDto dto
    ) {
        User user = getAuthenticatedUser();
        OrderResponseDto response = orderService.updateOrderStatus(orderId, dto.getStatus(), user.getId(), getAuthenticatedUserRole());
        return ResponseEntity.ok(response);
    }
}
