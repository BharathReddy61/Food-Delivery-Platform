package com.fooddelivery.controller;

import com.fooddelivery.dto.CreateOrderRequestDto;
import com.fooddelivery.dto.OrderResponseDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private Long getAuthenticatedUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        return user.getId();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(@Valid @RequestBody CreateOrderRequestDto dto) {
        Long userId = getAuthenticatedUserId();
        OrderResponseDto response = orderService.createOrder(userId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getUserOrders() {
        Long userId = getAuthenticatedUserId();
        List<OrderResponseDto> response = orderService.getUserOrders(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        Long userId = getAuthenticatedUserId();
        OrderResponseDto response = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(response);
    }
}
