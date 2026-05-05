package com.bridgelabz.appointmentservice.repository;

import com.bridgelabz.appointmentservice.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByProviderId(String providerId);

    Optional<Appointment> findBySlotId(String slotId);

    List<Appointment> findByStatus(String status);

    List<Appointment> findByProviderIdAndAppointmentDate(String providerId, LocalDate appointmentDate);

    List<Appointment> findByPatientIdAndAppointmentDateAfter(String patientId, LocalDate date);

    long countByProviderId(String providerId);
}