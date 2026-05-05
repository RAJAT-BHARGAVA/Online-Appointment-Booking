package com.bridgelabz.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private String appointmentId;
    private String patientId;
    private String providerId;
    private String slotId;
    private String serviceType;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
    private String modeOfConsultation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}