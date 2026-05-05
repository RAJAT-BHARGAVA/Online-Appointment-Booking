package com.bridgelabz.recordservice.service;

import com.bridgelabz.recordservice.dto.MedicalRecordRequest;
import com.bridgelabz.recordservice.dto.MedicalRecordResponse;
import com.bridgelabz.recordservice.dto.MedicalRecordUpdateRequest;
import com.bridgelabz.recordservice.exception.DuplicateMedicalRecordException;
import com.bridgelabz.recordservice.exception.MedicalRecordNotFoundException;
import com.bridgelabz.recordservice.model.MedicalRecord;
import com.bridgelabz.recordservice.repository.MedicalRecordRepository;
import com.bridgelabz.recordservice.service.impl.MedicalRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Medical Record Service Advanced Validation")
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private MedicalRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = MedicalRecord.builder()
                .recordId("REC-1")
                .appointmentId("AP-101")
                .patientId("PAT-1")
                .providerId("PRO-1")
                .diagnosis("Common Cold")
                .prescription("Rest and fluids")
                .build();
    }

    @Nested
    @DisplayName("Record Creation")
    class CreationTests {
        @Test
        @DisplayName("Should create record successfully when appointment is unique")
        void createRecord_Success() {
            MedicalRecordRequest request = MedicalRecordRequest.builder()
                    .appointmentId("AP-101")
                    .diagnosis("Common Cold")
                    .build();

            when(medicalRecordRepository.findByAppointmentId("AP-101")).thenReturn(Optional.empty());
            when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

            MedicalRecordResponse response = medicalRecordService.createRecord(request);

            assertNotNull(response);
            assertEquals("REC-1", response.getRecordId());
            verify(medicalRecordRepository).save(any(MedicalRecord.class));
        }

        @Test
        @DisplayName("Should throw DuplicateMedicalRecordException if record already exists")
        void createRecord_Fail_Duplicate() {
            MedicalRecordRequest request = MedicalRecordRequest.builder().appointmentId("AP-101").build();
            when(medicalRecordRepository.findByAppointmentId("AP-101")).thenReturn(Optional.of(testRecord));

            assertThrows(DuplicateMedicalRecordException.class, () -> medicalRecordService.createRecord(request));
        }
    }

    @Nested
    @DisplayName("Updates & Attachments")
    class UpdateTests {
        @Test
        @DisplayName("Should update diagnosis and prescription")
        void updateRecord_Success() {
            MedicalRecordUpdateRequest request = new MedicalRecordUpdateRequest();
            request.setDiagnosis("Influenza");
            request.setPrescription("Oseltamivir");

            when(medicalRecordRepository.findById("REC-1")).thenReturn(Optional.of(testRecord));
            when(medicalRecordRepository.save(testRecord)).thenReturn(testRecord);

            MedicalRecordResponse response = medicalRecordService.updateRecord("REC-1", request);

            assertEquals("Influenza", testRecord.getDiagnosis());
            verify(medicalRecordRepository).save(testRecord);
        }

        @Test
        @DisplayName("Should attach document URL successfully")
        void attachDocument_Success() {
            String url = "https://storage.oabs.com/scan1.pdf";
            when(medicalRecordRepository.findById("REC-1")).thenReturn(Optional.of(testRecord));
            when(medicalRecordRepository.save(testRecord)).thenReturn(testRecord);

            MedicalRecordResponse response = medicalRecordService.attachDocument("REC-1", url);

            assertEquals(url, testRecord.getAttachmentUrl());
        }
    }

    @Nested
    @DisplayName("Retrieval Operations")
    class RetrievalTests {
        @Test
        @DisplayName("Should fetch record by appointment ID")
        void getRecordByAppointment_Success() {
            when(medicalRecordRepository.findByAppointmentId("AP-101")).thenReturn(Optional.of(testRecord));
            MedicalRecordResponse response = medicalRecordService.getRecordByAppointment("AP-101");
            assertEquals("REC-1", response.getRecordId());
        }

        @Test
        @DisplayName("Should throw exception if record not found")
        void getRecordById_Fail_NotFound() {
            when(medicalRecordRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.getRecordById("invalid"));
        }
    }
}
