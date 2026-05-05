package com.bridgelabz.notificationservice.controller;

import com.bridgelabz.notificationservice.dto.BulkNotificationRequest;
import com.bridgelabz.notificationservice.dto.NotificationRequest;
import com.bridgelabz.notificationservice.dto.NotificationResponse;
import com.bridgelabz.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationResource {

    private final NotificationService notificationService;

    // Base GET: Get all notifications (Admin use)
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // Manual Send
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody NotificationRequest request) {
        return new ResponseEntity<>(notificationService.send(request), HttpStatus.CREATED);
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getByRecipient(recipientId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/recipient/{recipientId}/read-all")
    public ResponseEntity<Void> markAllRead(@PathVariable String recipientId) {
        notificationService.markAllRead(recipientId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipient/{recipientId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(recipientId));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        notificationService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<NotificationResponse>> sendBulk(@Valid @RequestBody BulkNotificationRequest request) {
        return new ResponseEntity<>(notificationService.sendBulk(request), HttpStatus.CREATED);
    }
}
