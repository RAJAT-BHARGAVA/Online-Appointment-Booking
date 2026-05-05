package com.bridgelabz.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private String notificationId;
    private String recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private String relatedId;
    private String relatedType;
    private Boolean isRead;
    private LocalDateTime sentAt;
}