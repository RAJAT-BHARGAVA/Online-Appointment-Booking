package com.bridgelabz.recordservice.controller;

import com.bridgelabz.recordservice.dto.MedicalRecordRequest;
import com.bridgelabz.recordservice.dto.MedicalRecordResponse;
import com.bridgelabz.recordservice.dto.MedicalRecordUpdateRequest;
import com.bridgelabz.recordservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createRecord(@Valid @RequestBody MedicalRecordRequest request) {
        return new ResponseEntity<>(medicalRecordService.createRecord(request), HttpStatus.CREATED);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordResponse> getRecordByAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(medicalRecordService.getRecordByAppointment(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getRecordsByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<MedicalRecordResponse>> getRecordsByProvider(@PathVariable String providerId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByProvider(providerId));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> updateRecord(@PathVariable String recordId,
                                                              @RequestBody MedicalRecordUpdateRequest request) {
        return ResponseEntity.ok(medicalRecordService.updateRecord(recordId, request));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String recordId) {
        medicalRecordService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> getRecordById(@PathVariable String recordId) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(recordId));
    }

    @GetMapping("/followup")
    public ResponseEntity<List<MedicalRecordResponse>> getFollowUpRecords(@RequestParam LocalDate followUpDate) {
        return ResponseEntity.ok(medicalRecordService.getFollowUpRecords(followUpDate));
    }

    @GetMapping("/count/{patientId}")
    public ResponseEntity<Long> getRecordCount(@PathVariable String patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordCount(patientId));
    }

    @PutMapping("/{recordId}/attachment")
    public ResponseEntity<MedicalRecordResponse> attachDocument(@PathVariable String recordId,
                                                                @RequestParam String attachmentUrl) {
        return ResponseEntity.ok(medicalRecordService.attachDocument(recordId, attachmentUrl));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll() {
        medicalRecordService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}