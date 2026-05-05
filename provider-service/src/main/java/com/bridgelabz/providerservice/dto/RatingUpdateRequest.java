package com.bridgelabz.providerservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingUpdateRequest {

    @NotNull(message = "avgRating is required")
    @DecimalMin(value = "0.0", message = "avgRating must be at least 0")
    @DecimalMax(value = "5.0", message = "avgRating must be at most 5")
    private Double avgRating;
}