package com.bridgelabz.appointmentservice.controller;

import com.bridgelabz.appointmentservice.dto.AppointmentRequest;
import com.bridgelabz.appointmentservice.dto.AppointmentResponse;
import com.bridgelabz.appointmentservice.dto.RescheduleRequest;
import com.bridgelabz.appointmentservice.dto.StatusUpdateRequest;
import com.bridgelabz.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.getById(appointmentId));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(appointmentService.getByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AppointmentResponse>> getByProvider(@PathVariable String providerId) {
        return ResponseEntity.ok(appointmentService.getByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/date")
    public ResponseEntity<List<AppointmentResponse>> getByProviderAndDate(@PathVariable String providerId,
                                                                          @RequestParam LocalDate date) {
        return ResponseEntity.ok(appointmentService.getByProviderAndDate(providerId, date));
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(@PathVariable String appointmentId,
                                                                     @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId, request));
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable String appointmentId,
                                                            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, request.getStatus()));
    }

    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(appointmentService.getUpcomingByPatient(patientId));
    }

    @GetMapping("/count/{providerId}")
    public ResponseEntity<Long> getAppointmentCount(@PathVariable String providerId) {
        return ResponseEntity.ok(appointmentService.getAppointmentCount(providerId));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll() {
        appointmentService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}