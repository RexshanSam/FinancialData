package com.finance.backend.service;

import com.finance.backend.dto.AuthResponse;
import com.finance.backend.dto.LoginRequest;
import com.finance.backend.dto.RegisterRequest;
import com.finance.backend.enums.Role;
import com.finance.backend.enums.Status;
import com.finance.backend.exception.ConflictException;
import org.springframework.security.authentication.BadCredentialsException;
import com.finance.backend.model.User;
import com.finance.backend.repository.UserRepository;
import com.finance.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        System.out.println("Registering new user with email: " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.VIEWER);
        user.setStatus(Status.ACTIVE);

        user = userRepository.save(user);
        System.out.println("User registered successfully with ID: " + user.getId());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        System.out.println("Login attempt for email: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != Status.ACTIVE) {
            throw new org.springframework.security.access.AccessDeniedException("Account is inactive");
        }

        System.out.println("User logged in successfully: " + user.getEmail());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        // Convert User entity to UserDetails
        UserDetails springUser = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );

        String token = jwtUtil.generateToken(springUser);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setName(user.getName());
        return response;
    }
}
