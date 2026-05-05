package com.bridgelabz.recordservice.repository;

import com.bridgelabz.recordservice.model.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {

    Optional<MedicalRecord> findByAppointmentId(String appointmentId);

    List<MedicalRecord> findByPatientId(String patientId);

    List<MedicalRecord> findByProviderId(String providerId);

    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(String patientId);

    List<MedicalRecord> findByFollowUpDate(LocalDate followUpDate);

    long countByPatientId(String patientId);

    void deleteByRecordId(String recordId);
}