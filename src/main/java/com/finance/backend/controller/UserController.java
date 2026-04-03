package com.finance.backend.controller;

import com.finance.backend.dto.UpdateRoleRequest;
import com.finance.backend.dto.UpdateStatusRequest;
import com.finance.backend.dto.UserResponse;
import com.finance.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users (Admin only)")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        System.out.println("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID (Admin only)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id
    ) {
        System.out.println("Fetching user with ID: " + id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user role (Admin only)")
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Role update request", required = true) @RequestBody UpdateRoleRequest request
    ) {
        System.out.println("Updating role for user ID: " + id);
        UserResponse user = userService.updateRole(id, request);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user status (Admin only)")
    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Status update request", required = true) @RequestBody UpdateStatusRequest request
    ) {
        System.out.println("Updating status for user ID: " + id);
        UserResponse user = userService.updateStatus(id, request);
        return ResponseEntity.ok(user);
    }
}
