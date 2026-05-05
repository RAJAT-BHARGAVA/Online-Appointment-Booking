package com.bridgelabz.recordservice.service;

import com.bridgelabz.recordservice.dto.MedicalRecordRequest;
import com.bridgelabz.recordservice.dto.MedicalRecordResponse;
import com.bridgelabz.recordservice.dto.MedicalRecordUpdateRequest;

import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordService {

    MedicalRecordResponse createRecord(MedicalRecordRequest request);

    MedicalRecordResponse getRecordByAppointment(String appointmentId);

    List<MedicalRecordResponse> getRecordsByPatient(String patientId);

    List<MedicalRecordResponse> getRecordsByProvider(String providerId);

    MedicalRecordResponse updateRecord(String recordId, MedicalRecordUpdateRequest request);

    void deleteRecord(String recordId);

    MedicalRecordResponse getRecordById(String recordId);

    List<MedicalRecordResponse> getFollowUpRecords(LocalDate followUpDate);

    long getRecordCount(String patientId);

    MedicalRecordResponse attachDocument(String recordId, String attachmentUrl);

    List<MedicalRecordResponse> getAllRecords();

    void deleteAll();
}