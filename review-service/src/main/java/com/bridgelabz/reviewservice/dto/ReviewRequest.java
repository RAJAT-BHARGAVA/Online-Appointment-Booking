package com.bridgelabz.reviewservice.dto;

import jakarta.validation.constraints.Max;
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
public class ReviewRequest {

    @NotBlank(message = "appointmentId is required")
    private String appointmentId;

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "providerId is required")
    private String providerId;

    @NotNull(message = "rating is required")
    @Min(value = 1, message = "rating must be at least 1")
    @Max(value = 5, message = "rating must be at most 5")
    private Integer rating;

    private String comment;

    @Builder.Default
    private Boolean isAnonymous = false;
}