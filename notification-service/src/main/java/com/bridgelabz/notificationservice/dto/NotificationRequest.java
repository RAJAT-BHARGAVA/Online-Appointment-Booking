package com.bridgelabz.notificationservice.dto;

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
public class NotificationRequest {

    @NotBlank(message = "recipientId is required")
    private String recipientId;

    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "message is required")
    private String message;

    @NotBlank(message = "channel is required")
    private String channel;

    private String relatedId;
    private String relatedType;

    @Builder.Default
    @NotNull(message = "isRead is required")
    private Boolean isRead = false;
}