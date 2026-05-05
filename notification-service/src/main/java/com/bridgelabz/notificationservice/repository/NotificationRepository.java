package com.bridgelabz.notificationservice.repository;

import com.bridgelabz.notificationservice.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByRecipientId(String recipientId);

    List<Notification> findByRecipientIdAndIsRead(String recipientId, Boolean isRead);

    long countByRecipientIdAndIsRead(String recipientId, Boolean isRead);

    List<Notification> findByType(String type);

    List<Notification> findByRelatedId(String relatedId);

    void deleteByNotificationId(String notificationId);
}