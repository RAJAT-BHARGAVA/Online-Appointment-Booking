package com.bridgelabz.authservice.controller;

import com.bridgelabz.authservice.dto.*;
import com.bridgelabz.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String email) {
        return ResponseEntity.ok(authService.logout(email));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.refreshToken(token));
    }

    // Matching frontend: api.get(`/auth/user/email/${email}`)
    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }

    // Matching frontend: api.get(`/auth/user/${id}`)
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    // Matching frontend: api.put(`/auth/profile/${id}`, data)
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable String userId,
                                                      @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(userId, request));
    }

    // Matching frontend: api.put(`/auth/password/${id}`, data)
    @PutMapping("/password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable String userId,
                                                 @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(userId, request));
    }

    // Matching frontend: api.put(`/auth/deactivate/${id}`)
    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<String> deactivateAccount(@PathVariable String userId) {
        return ResponseEntity.ok(authService.deactivateAccount(userId));
    }

    @PutMapping("/activate/{userId}")
    public ResponseEntity<String> activateAccount(@PathVariable String userId) {
        return ResponseEntity.ok(authService.activateAccount(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<java.util.List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<java.util.List<UserResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(authService.getUsersByRole(role));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll(@RequestParam String excludeId) {
        authService.deleteAll(excludeId);
        return ResponseEntity.noContent().build();
    }

    // ---------- Role based demo APIs ----------

    @GetMapping("/admin/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome Admin");
    }

    @GetMapping("/provider/dashboard")
    public ResponseEntity<String> providerDashboard() {
        return ResponseEntity.ok("Welcome Provider");
    }

    @GetMapping("/patient/dashboard")
    public ResponseEntity<String> patientDashboard() {
        return ResponseEntity.ok("Welcome Patient");
    }
}