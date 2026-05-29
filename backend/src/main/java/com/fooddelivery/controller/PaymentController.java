package com.fooddelivery.controller;

import com.fooddelivery.dto.CreatePaymentResponseDto;
import com.fooddelivery.dto.VerifyPaymentRequestDto;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @PostMapping("/create/{orderId}")
    public ResponseEntity<CreatePaymentResponseDto> createPayment(@PathVariable Long orderId) {
        User user = getAuthenticatedUser();
        CreatePaymentResponseDto response = paymentService.createPaymentOrder(user.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@Valid @RequestBody VerifyPaymentRequestDto dto) {
        paymentService.verifyPayment(dto);
        return ResponseEntity.ok("Payment verified successfully");
    }
}
