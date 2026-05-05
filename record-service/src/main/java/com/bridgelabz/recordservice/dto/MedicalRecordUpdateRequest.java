package com.bridgelabz.recordservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordUpdateRequest {

    private String diagnosis;
    private String prescription;
    private String notes;
    private String attachmentUrl;
    private LocalDate followUpDate;
}