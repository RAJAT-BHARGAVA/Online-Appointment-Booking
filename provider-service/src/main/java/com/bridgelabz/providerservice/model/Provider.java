package com.bridgelabz.providerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    private String providerId;

    private String userId; // Changed from Long to String
    private String fullName;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private String bio;
    private String clinicName;
    private String clinicAddress;
    private Double consultationFee;

    @Builder.Default
    private Double avgRating = 0.0;

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}