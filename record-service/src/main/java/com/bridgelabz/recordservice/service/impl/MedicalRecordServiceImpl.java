package com.bridgelabz.recordservice.service.impl;

import com.bridgelabz.recordservice.dto.MedicalRecordRequest;
import com.bridgelabz.recordservice.dto.MedicalRecordResponse;
import com.bridgelabz.recordservice.dto.MedicalRecordUpdateRequest;
import com.bridgelabz.recordservice.exception.DuplicateMedicalRecordException;
import com.bridgelabz.recordservice.exception.MedicalRecordNotFoundException;
import com.bridgelabz.recordservice.model.MedicalRecord;
import com.bridgelabz.recordservice.repository.MedicalRecordRepository;
import com.bridgelabz.recordservice.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public MedicalRecordResponse createRecord(MedicalRecordRequest request) {
        if (medicalRecordRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new DuplicateMedicalRecordException(
                    "Medical record already exists for appointmentId: " + request.getAppointmentId()
            );
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .notes(request.getNotes())
                .attachmentUrl(request.getAttachmentUrl())
                .followUpDate(request.getFollowUpDate())
                .build();

        return mapToResponse(medicalRecordRepository.save(record));
    }

    @Override
    public MedicalRecordResponse getRecordByAppointment(String appointmentId) {
        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found for appointmentId: " + appointmentId));

        return mapToResponse(record);
    }

    @Override
    public List<MedicalRecordResponse> getRecordsByPatient(String patientId) {
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<MedicalRecordResponse> getRecordsByProvider(String providerId) {
        return medicalRecordRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MedicalRecordResponse updateRecord(String recordId, MedicalRecordUpdateRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + recordId));

        record.setDiagnosis(request.getDiagnosis());
        record.setPrescription(request.getPrescription());
        record.setNotes(request.getNotes());
        record.setAttachmentUrl(request.getAttachmentUrl());
        record.setFollowUpDate(request.getFollowUpDate());
        record.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(medicalRecordRepository.save(record));
    }

    @Override
    public void deleteRecord(String recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + recordId));

        medicalRecordRepository.delete(record);
    }

    @Override
    public MedicalRecordResponse getRecordById(String recordId) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + recordId));

        return mapToResponse(record);
    }

    @Override
    public List<MedicalRecordResponse> getFollowUpRecords(LocalDate followUpDate) {
        return medicalRecordRepository.findByFollowUpDate(followUpDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getRecordCount(String patientId) {
        return medicalRecordRepository.countByPatientId(patientId);
    }

    @Override
    public MedicalRecordResponse attachDocument(String recordId, String attachmentUrl) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record not found with id: " + recordId));

        record.setAttachmentUrl(attachmentUrl);
        record.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(medicalRecordRepository.save(record));
    }

    @Override
    public List<MedicalRecordResponse> getAllRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAll() {
        medicalRecordRepository.deleteAll();
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .recordId(record.getRecordId())
                .appointmentId(record.getAppointmentId())
                .patientId(record.getPatientId())
                .providerId(record.getProviderId())
                .diagnosis(record.getDiagnosis())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .attachmentUrl(record.getAttachmentUrl())
                .followUpDate(record.getFollowUpDate())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}