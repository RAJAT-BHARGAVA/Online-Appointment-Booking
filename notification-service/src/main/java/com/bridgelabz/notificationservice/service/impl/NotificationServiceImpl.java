package com.bridgelabz.notificationservice.service.impl;

import com.bridgelabz.notificationservice.dto.*;
import com.bridgelabz.notificationservice.exception.NotificationNotFoundException;
import com.bridgelabz.notificationservice.model.Notification;
import com.bridgelabz.notificationservice.repository.NotificationRepository;
import com.bridgelabz.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Override
    public NotificationResponse send(NotificationRequest request) {
        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .channel(request.getChannel())
                .relatedId(request.getRelatedId())
                .relatedType(request.getRelatedType())
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        if ("EMAIL".equalsIgnoreCase(request.getChannel())) {
            sendEmail(request.getRecipientId(), request.getTitle(), request.getMessage());
        } else if ("SMS".equalsIgnoreCase(request.getChannel())) {
            sendSMS(request.getRecipientId(), request.getMessage());
        }

        return mapToResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> sendBulk(BulkNotificationRequest request) {
        log.info("Sending bulk notification to {} recipients", request.getRecipientIds().size());
        return request.getRecipientIds().stream()
                .map(id -> {
                    String cleanId = id.trim();
                    Notification notification = Notification.builder()
                            .recipientId(cleanId)
                            .type(request.getType())
                            .title(request.getTitle())
                            .message(request.getMessage())
                            .channel(request.getChannel())
                            .relatedId(request.getRelatedId())
                            .relatedType(request.getRelatedType())
                            .isRead(false)
                            .sentAt(LocalDateTime.now())
                            .build();
                    return mapToResponse(notificationRepository.save(notification));
                }).toList();
    }

    @Override
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllRead(String recipientId) {
        // Mark personal notifications as read
        List<Notification> unreadPersonal = notificationRepository.findByRecipientIdAndIsRead(recipientId.trim(), false);
        unreadPersonal.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadPersonal);
        
        // Also mark global notifications as read (Simplified for this environment)
        List<Notification> unreadGlobal = notificationRepository.findByRecipientIdAndIsRead("ALL", false);
        unreadGlobal.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadGlobal);
    }

    @Override
    public List<NotificationResponse> getByRecipient(String recipientId) {
        String cleanId = recipientId != null ? recipientId.trim() : "";
        log.info("Fetching notifications for recipientId: [{}]", cleanId);
        
        // Fetch personal notifications
        List<Notification> personal = notificationRepository.findByRecipientId(cleanId);
        
        // Fetch global broadcast notifications
        List<Notification> global = notificationRepository.findByRecipientId("ALL");
        
        List<Notification> combined = new java.util.ArrayList<>(personal);
        combined.addAll(global);
        
        return combined.stream()
                .sorted((a, b) -> b.getSentAt().compareTo(a.getSentAt()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getUnreadCount(String recipientId) {
        String cleanId = recipientId != null ? recipientId.trim() : "";
        long personalCount = notificationRepository.countByRecipientIdAndIsRead(cleanId, false);
        long globalCount = notificationRepository.countByRecipientIdAndIsRead("ALL", false);
        return personalCount + globalCount;
    }

    @Override
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public void deleteAll() {
        notificationRepository.deleteAll();
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    @Override
    public void sendSMS(String to, String body) {
        log.info("SMS sent to: {} | Message: {}", to, body);
    }

    @Override
    public List<NotificationResponse> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .recipientId(notification.getRecipientId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .channel(notification.getChannel())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .isRead(notification.getIsRead())
                .sentAt(notification.getSentAt())
                .build();
    }
}