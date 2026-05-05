package com.bridgelabz.providerservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    private String fullName;
    private String specialization;
    private String qualification;
    
    @NotNull(message = "experienceYears is required")
    @Min(value = 0, message = "experienceYears cannot be negative")
    private Integer experienceYears;

    private String bio;
    private String clinicName;
    private String clinicAddress;
    private Double consultationFee;
}