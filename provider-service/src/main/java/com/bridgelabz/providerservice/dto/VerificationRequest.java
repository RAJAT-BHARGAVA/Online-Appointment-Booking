package com.bridgelabz.providerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @NotNull(message = "isVerified is required")
    private Boolean isVerified;
}