package com.bridgelabz.recordservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequest {

    @NotBlank(message = "appointmentId is required")
    private String appointmentId;

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "providerId is required")
    private String providerId;

    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
}