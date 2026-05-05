package com.bridgelabz.providerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponse {
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
    private Double avgRating;
    private Boolean isVerified;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
}