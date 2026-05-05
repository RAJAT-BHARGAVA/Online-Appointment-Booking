package com.bridgelabz.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNotificationRequest {

    @NotEmpty(message = "recipientIds are required")
    private List<String> recipientIds;

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
}