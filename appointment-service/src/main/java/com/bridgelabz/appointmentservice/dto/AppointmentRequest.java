package com.bridgelabz.appointmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "providerId is required")
    private String providerId;

    @NotBlank(message = "slotId is required")
    private String slotId;

    @NotBlank(message = "serviceType is required")
    private String serviceType;

    @NotNull(message = "appointmentDate is required")
    private LocalDate appointmentDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    private String notes;

    @NotBlank(message = "modeOfConsultation is required")
    private String modeOfConsultation;
}