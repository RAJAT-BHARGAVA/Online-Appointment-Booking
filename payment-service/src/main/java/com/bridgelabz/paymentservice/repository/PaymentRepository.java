package com.bridgelabz.paymentservice.repository;

import com.bridgelabz.paymentservice.model.Payment;
import com.bridgelabz.paymentservice.model.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByAppointmentId(String appointmentId);

    List<Payment> findByPatientId(String patientId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByProviderId(String providerId);

    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(PaymentStatus status);
}