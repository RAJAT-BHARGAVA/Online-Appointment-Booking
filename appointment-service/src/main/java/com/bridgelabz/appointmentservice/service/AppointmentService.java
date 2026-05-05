package com.bridgelabz.appointmentservice.service;

import com.bridgelabz.appointmentservice.dto.AppointmentRequest;
import com.bridgelabz.appointmentservice.dto.AppointmentResponse;
import com.bridgelabz.appointmentservice.dto.RescheduleRequest;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse bookAppointment(AppointmentRequest request);

    AppointmentResponse getById(String appointmentId);

    List<AppointmentResponse> getByPatient(String patientId);

    List<AppointmentResponse> getByProvider(String providerId);

    List<AppointmentResponse> getByProviderAndDate(String providerId, LocalDate date);

    AppointmentResponse cancelAppointment(String appointmentId);

    AppointmentResponse rescheduleAppointment(String appointmentId, RescheduleRequest request);

    AppointmentResponse completeAppointment(String appointmentId);

    AppointmentResponse updateStatus(String appointmentId, String status);

    List<AppointmentResponse> getUpcomingByPatient(String patientId);

    long getAppointmentCount(String providerId);

    List<AppointmentResponse> getAllAppointments();

    void deleteAppointment(String appointmentId);

    void deleteAll();
}