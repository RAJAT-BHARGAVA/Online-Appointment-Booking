package com.bridgelabz.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String notificationId;

    private String recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private String relatedId;
    private String relatedType;

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}