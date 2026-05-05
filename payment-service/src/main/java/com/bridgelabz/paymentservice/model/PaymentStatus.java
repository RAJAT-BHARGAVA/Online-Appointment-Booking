package com.bridgelabz.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,
    REFUND_REQUESTED,
    REFUND_REJECTED,
    SUCCESS,
    Pending,
    Paid,
    Failed,
    Refunded,
    Success;

    @JsonCreator
    public static PaymentStatus fromString(String value) {
        if (value == null) return null;
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING; // Default fallback
        }
    }
}