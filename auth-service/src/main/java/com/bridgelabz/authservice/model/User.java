package com.bridgelabz.authservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private String role;
    private String provider;
    private boolean isActive;
    private LocalDateTime createdAt;
    private String profilePicUrl;
}