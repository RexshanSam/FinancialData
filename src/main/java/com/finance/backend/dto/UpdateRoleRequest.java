package com.finance.backend.dto;

import com.finance.backend.enums.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;

    public UpdateRoleRequest() {}

    public UpdateRoleRequest(Role role) {
        this.role = role;
    }

    // Getters and setters
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
