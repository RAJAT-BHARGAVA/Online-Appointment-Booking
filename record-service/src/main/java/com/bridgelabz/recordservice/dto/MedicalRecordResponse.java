package com.bridgelabz.recordservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {

    private String recordId;
    private String appointmentId;
    private String patientId;
    private String providerId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}