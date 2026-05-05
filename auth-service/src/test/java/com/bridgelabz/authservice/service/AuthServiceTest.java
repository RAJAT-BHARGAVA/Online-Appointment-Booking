package com.bridgelabz.authservice.service;

import com.bridgelabz.authservice.dto.*;
import com.bridgelabz.authservice.exception.ResourceNotFoundException;
import com.bridgelabz.authservice.model.User;
import com.bridgelabz.authservice.repository.UserRepository;
import com.bridgelabz.authservice.security.JwtUtil;
import com.bridgelabz.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Comprehensive Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId("U100")
                .fullName("Antigravity Tester")
                .email("test@antigravity.ai")
                .passwordHash("hashed_pwd")
                .role("PATIENT")
                .isActive(true)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Antigravity Tester");
        registerRequest.setEmail("test@antigravity.ai");
        registerRequest.setPassword("password123");
        registerRequest.setRole("PATIENT");
    }

    @Nested
    @DisplayName("Registration Flow")
    class RegistrationTests {
        @Test
        @DisplayName("Should register new user successfully")
        void register_Success() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashed_pwd");
            when(jwtUtil.generateToken(anyString(), any(), anyString(), anyString())).thenReturn("mocked_jwt_token");

            AuthResponse response = authService.register(registerRequest);

            assertNotNull(response);
            assertEquals("User registered successfully", response.getMessage());
            assertEquals("mocked_jwt_token", response.getToken());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail when email already exists")
        void register_Fail_DuplicateEmail() {
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            AuthResponse response = authService.register(registerRequest);

            assertEquals("Email already exists", response.getMessage());
            assertNull(response.getToken());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Flow")
    class LoginTests {
        @Test
        @DisplayName("Should login successfully with correct credentials")
        void login_Success() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@antigravity.ai");
            loginRequest.setPassword("password123");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "hashed_pwd")).thenReturn(true);
            when(jwtUtil.generateToken(anyString(), any(), anyString(), anyString())).thenReturn("new_token");

            AuthResponse response = authService.login(loginRequest);

            assertEquals("Login successful", response.getMessage());
            assertNotNull(response.getToken());
        }

        @Test
        @DisplayName("Should fail login if user is deactivated")
        void login_Fail_Deactivated() {
            testUser.setActive(false);
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@antigravity.ai");
            loginRequest.setPassword("password123");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

            AuthResponse response = authService.login(loginRequest);

            assertEquals("Account is deactivated", response.getMessage());
            assertNull(response.getToken());
        }

        @Test
        @DisplayName("Should fail login if password doesn't match")
        void login_Fail_WrongPassword() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@antigravity.ai");
            loginRequest.setPassword("wrong_pwd");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrong_pwd", "hashed_pwd")).thenReturn(false);

            AuthResponse response = authService.login(loginRequest);

            assertEquals("Invalid password", response.getMessage());
            assertNull(response.getToken());
        }
    }

    @Nested
    @DisplayName("Password Management")
    class PasswordTests {
        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("old_pwd");
            request.setNewPassword("new_pwd");

            when(userRepository.findById("U100")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("old_pwd", "hashed_pwd")).thenReturn(true);
            when(passwordEncoder.encode("new_pwd")).thenReturn("new_hashed_pwd");

            String result = authService.changePassword("U100", request);

            assertEquals("Password changed successfully", result);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception if old password is wrong")
        void changePassword_Fail_WrongOldPassword() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("wrong_old_pwd");

            when(userRepository.findById("U100")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrong_old_pwd", "hashed_pwd")).thenReturn(false);

            assertThrows(RuntimeException.class, () -> authService.changePassword("U100", request));
        }
    }

    @Nested
    @DisplayName("Profile Management")
    class ProfileTests {
        @Test
        @DisplayName("Should update profile successfully")
        void updateProfile_Success() {
            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setFullName("Updated Name");
            request.setPhone("9999999999");

            when(userRepository.findById("U100")).thenReturn(Optional.of(testUser));

            UserResponse response = authService.updateProfile("U100", request);

            assertEquals("Updated Name", response.getFullName());
            assertEquals("9999999999", response.getPhone());
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception if user not found for profile update")
        void updateProfile_Fail_UserNotFound() {
            when(userRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> authService.updateProfile("invalid", new UpdateProfileRequest()));
        }
    }
}
