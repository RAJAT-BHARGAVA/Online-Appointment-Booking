package com.bridgelabz.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private String relatedId;
    private String relatedType;
}
