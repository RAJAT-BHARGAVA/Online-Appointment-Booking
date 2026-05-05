package com.bridgelabz.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayOrderRequest {
    private double amount;
    private String currency;
    private String appointmentId;
}
