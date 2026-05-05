package com.bridgelabz.notificationservice.service;

import com.bridgelabz.notificationservice.dto.*;
import com.bridgelabz.notificationservice.exception.NotificationNotFoundException;
import com.bridgelabz.notificationservice.model.Notification;
import com.bridgelabz.notificationservice.repository.NotificationRepository;
import com.bridgelabz.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Advanced Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .notificationId("NOT-1")
                .recipientId("user@test.com")
                .type("BOOKING")
                .title("Alert")
                .message("Test Message")
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Notification Delivery")
    class DeliveryTests {
        @Test
        @DisplayName("Should send Email and save to DB when channel is EMAIL")
        void send_EmailChannel_Success() {
            NotificationRequest request = NotificationRequest.builder()
                    .recipientId("user@test.com")
                    .channel("EMAIL")
                    .title("Email Title")
                    .message("Body")
                    .build();

            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            NotificationResponse response = notificationService.send(request);

            assertNotNull(response);
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("Should process bulk notifications for multiple users")
        void sendBulk_Success() {
            BulkNotificationRequest request = new BulkNotificationRequest();
            request.setRecipientIds(List.of("user1", "user2", "user3"));
            request.setTitle("Bulk Title");

            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            List<NotificationResponse> results = notificationService.sendBulk(request);

            assertEquals(3, results.size());
            verify(notificationRepository, times(3)).save(any(Notification.class));
        }
    }

    @Nested
    @DisplayName("Recipient Logic & Broadcasts")
    class RecipientTests {
        @Test
        @DisplayName("Should fetch both personal and global (ALL) notifications")
        void getByRecipient_IncludesGlobal() {
            Notification globalNotif = Notification.builder().recipientId("ALL").sentAt(LocalDateTime.now()).build();
            
            when(notificationRepository.findByRecipientId("user1")).thenReturn(List.of(testNotification));
            when(notificationRepository.findByRecipientId("ALL")).thenReturn(List.of(globalNotif));

            List<NotificationResponse> results = notificationService.getByRecipient("user1");

            assertEquals(2, results.size());
            verify(notificationRepository).findByRecipientId("user1");
            verify(notificationRepository).findByRecipientId("ALL");
        }

        @Test
        @DisplayName("Should calculate combined unread count (Personal + ALL)")
        void getUnreadCount_SumSuccess() {
            when(notificationRepository.countByRecipientIdAndIsRead("user1", false)).thenReturn(5L);
            when(notificationRepository.countByRecipientIdAndIsRead("ALL", false)).thenReturn(2L);

            long count = notificationService.getUnreadCount("user1");

            assertEquals(7L, count);
        }
    }

    @Nested
    @DisplayName("Read/Write Operations")
    class StatusTests {
        @Test
        @DisplayName("Should mark notification as read successfully")
        void markAsRead_Success() {
            when(notificationRepository.findById("NOT-1")).thenReturn(Optional.of(testNotification));
            
            notificationService.markAsRead("NOT-1");

            assertTrue(testNotification.getIsRead());
            verify(notificationRepository).save(testNotification);
        }

        @Test
        @DisplayName("Should throw exception if notification ID is missing")
        void markAsRead_Fail_NotFound() {
            when(notificationRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead("invalid"));
        }
    }
}
