package com.bridgelabz.paymentservice.dto;

import com.bridgelabz.paymentservice.model.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "appointmentId is required")
    private String appointmentId;

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "providerId is required")
    private String providerId;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private Double amount;

    @NotNull(message = "mode is required")
    private PaymentMode mode;

    private com.bridgelabz.paymentservice.model.PaymentStatus status;

    @NotBlank(message = "currency is required")
    private String currency;

    private String notes;
}