package com.bridgelabz.appointmentservice.publisher;

import com.bridgelabz.appointmentservice.config.RabbitMQConfig;
import com.bridgelabz.appointmentservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final RabbitTemplate template;

    public void publishNotification(NotificationRequest request) {
        log.info("Publishing notification to RabbitMQ: {}", request);
        try {
            template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, request);
        } catch (Exception e) {
            log.error("Failed to publish notification: {}", e.getMessage());
        }
    }
}
