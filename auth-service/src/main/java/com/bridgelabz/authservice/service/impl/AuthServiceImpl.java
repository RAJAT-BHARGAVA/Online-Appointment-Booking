package com.bridgelabz.authservice.service.impl;

import com.bridgelabz.authservice.dto.*;
import com.bridgelabz.authservice.exception.ResourceNotFoundException;
import com.bridgelabz.authservice.model.User;
import com.bridgelabz.authservice.repository.UserRepository;
import com.bridgelabz.authservice.security.JwtUtil;
import com.bridgelabz.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .message("Email already exists")
                    .token(null)
                    .build();
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .provider(request.getProvider() != null ? request.getProvider() : "LOCAL")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .profilePicUrl(request.getProfilePicUrl())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId(), user.getRole(), user.getFullName());

        return AuthResponse.builder()
                .message("User registered successfully")
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return AuthResponse.builder()
                    .message("User not found")
                    .token(null)
                    .build();
        }

        if (!user.isActive()) {
            return AuthResponse.builder()
                    .message("Account is deactivated")
                    .token(null)
                    .build();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return AuthResponse.builder()
                    .message("Invalid password")
                    .token(null)
                    .build();
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserId(), user.getRole(), user.getFullName());

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .build();
    }

    @Override
    public String logout(String email) {
        return "Logout successful for: " + email;
    }

    @Override
    public String validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            return "Valid token for: " + email;
        } catch (Exception e) {
            return "Invalid token";
        }
    }

    @Override
    public AuthResponse refreshToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            String newToken = "";
            if (user != null) {
                newToken = jwtUtil.generateToken(email, user.getUserId(), user.getRole(), user.getFullName());
            } else {
                newToken = jwtUtil.generateToken(email, "", "", "");
            }

            return AuthResponse.builder()
                    .message("Token refreshed successfully")
                    .token(newToken)
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .message("Invalid token")
                    .token(null)
                    .build();
        }
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setProfilePicUrl(request.getProfilePicUrl());

        userRepository.save(user);

        return mapToUserResponse(user);
    }

    @Override
    public String changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }

    @Override
    public String deactivateAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(false);
        userRepository.save(user);

        return "Account deactivated successfully";
    }

    @Override
    public String activateAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        return "Account activated successfully";
    }

    @Override
    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<UserResponse> getUsersByRole(String role) {
        return userRepository.findAllByRole(role).stream()
                .map(this::mapToUserResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .provider(user.getProvider())
                .isActive(user.isActive())
                .profilePicUrl(user.getProfilePicUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void deleteAll(String excludeId) {
        userRepository.findAll().forEach(user -> {
            if (excludeId != null && !user.getUserId().equals(excludeId)) {
                userRepository.delete(user);
            } else if (excludeId == null) {
                // If no excludeId, safety first: don't delete anyone or log warning
                System.out.println("Warning: deleteAll called without excludeId");
            }
        });
    }
}