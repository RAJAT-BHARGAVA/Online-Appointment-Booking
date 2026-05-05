package com.bridgelabz.notificationservice.service;

import com.bridgelabz.notificationservice.dto.*;

import java.util.List;

public interface NotificationService {

    NotificationResponse send(NotificationRequest request);
    List<NotificationResponse> sendBulk(BulkNotificationRequest request);
    void markAsRead(String notificationId);
    void markAllRead(String recipientId);
    List<NotificationResponse> getByRecipient(String recipientId);
    long getUnreadCount(String recipientId);
    void deleteNotification(String notificationId);
    void deleteAll();
    void sendEmail(String to, String subject, String body);
    void sendSMS(String to, String body);
    List<NotificationResponse> getAll();
}