package com.bridgelabz.appointmentservice.service.impl;

import com.bridgelabz.appointmentservice.dto.AppointmentRequest;
import com.bridgelabz.appointmentservice.dto.AppointmentResponse;
import com.bridgelabz.appointmentservice.dto.RescheduleRequest;
import com.bridgelabz.appointmentservice.exception.AppointmentNotFoundException;
import com.bridgelabz.appointmentservice.model.Appointment;
import com.bridgelabz.appointmentservice.repository.AppointmentRepository;
import com.bridgelabz.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final com.bridgelabz.appointmentservice.publisher.NotificationPublisher notificationPublisher;

    @Override
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .slotId(request.getSlotId())
                .serviceType(request.getServiceType())
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("SCHEDULED")
                .notes(request.getNotes())
                .modeOfConsultation(request.getModeOfConsultation())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            // Notify Patient
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getPatientId())
                    .type("BOOKING")
                    .title("Appointment Booked Successfully")
                    .message("Your appointment for " + savedAppointment.getServiceType() + " on " + savedAppointment.getAppointmentDate() + " has been scheduled.")
                    .channel("EMAIL")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());

            // Notify Provider
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getProviderId())
                    .type("BOOKING")
                    .title("New Appointment Received")
                    .message("A new appointment for " + savedAppointment.getServiceType() + " has been booked for " + savedAppointment.getAppointmentDate())
                    .channel("APP")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send booking notifications: " + e.getMessage());
        }

        return mapToResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse getById(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        return mapToResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getByProvider(String providerId) {
        return appointmentRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getByProviderAndDate(String providerId, LocalDate date) {
        return appointmentRepository.findByProviderIdAndAppointmentDate(providerId, date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AppointmentResponse cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        appointment.setStatus("CANCELLED");
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            // Notify Patient
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getPatientId())
                    .type("CANCELLATION")
                    .title("Appointment Cancelled")
                    .message("Your appointment on " + savedAppointment.getAppointmentDate() + " has been cancelled.")
                    .channel("EMAIL")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());

            // Notify Provider
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getProviderId())
                    .type("CANCELLATION")
                    .title("Appointment Cancelled by Patient")
                    .message("The appointment scheduled for " + savedAppointment.getAppointmentDate() + " has been cancelled by the patient.")
                    .channel("APP")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notifications: " + e.getMessage());
        }

        return mapToResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setStatus("RESCHEDULED");
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            // Publish notification
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getPatientId())
                    .type("REMINDER")
                    .title("Appointment Rescheduled")
                    .message("Your appointment has been rescheduled to " + savedAppointment.getAppointmentDate() + " at " + savedAppointment.getStartTime())
                    .channel("EMAIL")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send reschedule notification: " + e.getMessage());
        }

        return mapToResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse completeAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        appointment.setStatus("COMPLETED");
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            // Publish notification
            notificationPublisher.publishNotification(com.bridgelabz.appointmentservice.dto.NotificationRequest.builder()
                    .recipientId(savedAppointment.getPatientId())
                    .type("FOLLOWUP")
                    .title("Appointment Completed")
                    .message("Your appointment on " + savedAppointment.getAppointmentDate() + " has been marked as completed. Please share your feedback.")
                    .channel("EMAIL")
                    .relatedId(savedAppointment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send completion notification: " + e.getMessage());
        }

        return mapToResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse updateStatus(String appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + appointmentId));

        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentResponse> getUpcomingByPatient(String patientId) {
        return appointmentRepository.findByPatientIdAndAppointmentDateAfter(patientId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getAppointmentCount(String providerId) {
        return appointmentRepository.countByProviderId(providerId);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAppointment(String appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    @Override
    public void deleteAll() {
        appointmentRepository.deleteAll();
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .providerId(appointment.getProviderId())
                .slotId(appointment.getSlotId())
                .serviceType(appointment.getServiceType())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .modeOfConsultation(appointment.getModeOfConsultation())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}