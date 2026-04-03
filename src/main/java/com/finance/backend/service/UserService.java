package com.finance.backend.service;

import com.finance.backend.dto.UserResponse;
import com.finance.backend.enums.Role;
import com.finance.backend.enums.Status;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.model.User;
import com.finance.backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        System.out.println("Fetching all users");
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(convertToResponse(user));
        }
        return responses;
    }

    public UserResponse getUserById(Long id) {
        System.out.println("Fetching user with ID: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToResponse(user);
    }

    public UserResponse updateRole(Long id, com.finance.backend.dto.UpdateRoleRequest request) {
        System.out.println("Updating role for user ID: " + id + " to role: " + request.getRole());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(request.getRole());
        user = userRepository.save(user);
        System.out.println("Role updated successfully for user: " + user.getEmail());
        return convertToResponse(user);
    }

    public UserResponse updateStatus(Long id, com.finance.backend.dto.UpdateStatusRequest request) {
        System.out.println("Updating status for user ID: " + id + " to status: " + request.getStatus());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(request.getStatus());
        user = userRepository.save(user);
        System.out.println("Status updated successfully for user: " + user.getEmail());
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
