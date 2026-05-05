package com.bridgelabz.authservice.service;

import com.bridgelabz.authservice.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String logout(String email);

    String validateToken(String token);

    AuthResponse refreshToken(String token);

    UserResponse getUserByEmail(String email);

    UserResponse getUserById(String userId);

    UserResponse updateProfile(String userId, UpdateProfileRequest request);

    String changePassword(String userId, ChangePasswordRequest request);

    String deactivateAccount(String userId);

    String activateAccount(String userId);
    
    java.util.List<UserResponse> getAllUsers();

    java.util.List<UserResponse> getUsersByRole(String role);

    void deleteUser(String userId);

    void deleteAll(String excludeId);
}