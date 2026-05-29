package com.fooddelivery.controller;

import com.fooddelivery.dto.LoginRequest;
import com.fooddelivery.dto.RegisterRequest;
import com.fooddelivery.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(
            @Valid @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }
}