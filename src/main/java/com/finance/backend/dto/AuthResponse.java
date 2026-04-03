package com.finance.backend.dto;

public class AuthResponse {
    private String token;
    private String type;
    private String email;
    private String role;
    private String name;

    public AuthResponse() {}

    public AuthResponse(String token, String type, String email, String role, String name) {
        this.token = token;
        this.type = type;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
