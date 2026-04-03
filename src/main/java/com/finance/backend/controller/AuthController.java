package com.finance.backend.controller;

import com.finance.backend.dto.AuthResponse;
import com.finance.backend.dto.LoginRequest;
import com.finance.backend.dto.RegisterRequest;
import com.finance.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "Registration request", required = true)
            @Valid @RequestBody RegisterRequest request
    ) {
        System.out.println("Received registration request for email: " + request.getEmail());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login request", required = true)
            @Valid @RequestBody LoginRequest request
    ) {
        System.out.println("Received login request for email: " + request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
