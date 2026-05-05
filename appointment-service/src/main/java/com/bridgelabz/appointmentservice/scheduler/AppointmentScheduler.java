package com.bridgelabz.appointmentservice.scheduler;

import com.bridgelabz.appointmentservice.model.Appointment;
import com.bridgelabz.appointmentservice.repository.AppointmentRepository;
import com.bridgelabz.appointmentservice.publisher.NotificationPublisher;
import com.bridgelabz.appointmentservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final NotificationPublisher notificationPublisher;

    // Run every 15 minutes to check for upcoming appointments
    @Scheduled(fixedRate = 900000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        
        // 24-Hour Reminders
        LocalDateTime target24h = now.plusHours(24);
        sendRemindersForTimeRange(target24h, "REMINDER", "24-Hour Reminder");

        // 1-Hour Reminders
        LocalDateTime target1h = now.plusHours(1);
        sendRemindersForTimeRange(target1h, "REMINDER", "1-Hour Reminder");
    }

    private void sendRemindersForTimeRange(LocalDateTime targetTime, String type, String title) {
        // Simple logic: fetch all 'Scheduled' appointments for that hour
        // In a real app, you'd track if a reminder was already sent to avoid duplicates
        List<Appointment> upcoming = appointmentRepository.findByStatus("Scheduled");
        
        for (Appointment appt : upcoming) {
            LocalDateTime apptDateTime = LocalDateTime.of(appt.getAppointmentDate(), appt.getStartTime());
            
            // If appointment is within the next 15-minute window of the target time
            if (apptDateTime.isAfter(targetTime.minusMinutes(15)) && apptDateTime.isBefore(targetTime.plusMinutes(15))) {
                log.info("Sending {} for appointment {}", title, appt.getAppointmentId());
                
                // Notify Patient
                notificationPublisher.publishNotification(NotificationRequest.builder()
                        .recipientId(appt.getPatientId())
                        .type(type)
                        .title(title)
                        .message("Your appointment for " + appt.getServiceType() + " is scheduled for " + appt.getStartTime() + " tomorrow.")
                        .channel("EMAIL")
                        .relatedId(appt.getAppointmentId())
                        .relatedType("APPOINTMENT")
                        .build());
            }
        }
    }
}
