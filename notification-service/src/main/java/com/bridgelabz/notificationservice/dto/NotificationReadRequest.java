package com.bridgelabz.notificationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadRequest {

    @NotNull(message = "isRead is required")
    private Boolean isRead;
}