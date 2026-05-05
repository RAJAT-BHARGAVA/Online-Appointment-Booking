package com.bridgelabz.paymentservice.dto;

import com.bridgelabz.paymentservice.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {

    @NotNull(message = "status is required")
    private PaymentStatus status;
}