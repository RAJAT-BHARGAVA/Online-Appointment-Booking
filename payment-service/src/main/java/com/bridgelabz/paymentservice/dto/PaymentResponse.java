package com.bridgelabz.paymentservice.dto;

import com.bridgelabz.paymentservice.model.PaymentMode;
import com.bridgelabz.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String paymentId;
    private String appointmentId;
    private String patientId;
    private String providerId;
    private Double amount;
    private PaymentStatus status;
    private PaymentMode mode;
    private String transactionId;
    private String currency;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String notes;
}