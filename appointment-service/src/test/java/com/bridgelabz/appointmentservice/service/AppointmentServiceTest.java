package com.bridgelabz.appointmentservice.service;

import com.bridgelabz.appointmentservice.dto.AppointmentRequest;
import com.bridgelabz.appointmentservice.dto.AppointmentResponse;
import com.bridgelabz.appointmentservice.dto.RescheduleRequest;
import com.bridgelabz.appointmentservice.exception.AppointmentNotFoundException;
import com.bridgelabz.appointmentservice.model.Appointment;
import com.bridgelabz.appointmentservice.repository.AppointmentRepository;
import com.bridgelabz.appointmentservice.publisher.NotificationPublisher;
import com.bridgelabz.appointmentservice.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment Service Deep Testing")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testAppointment = Appointment.builder()
                .appointmentId("AP-101")
                .patientId("PAT-001")
                .providerId("PRO-999")
                .slotId("SLOT-77")
                .serviceType("Consultation")
                .appointmentDate(LocalDate.now().plusDays(2))
                .status("SCHEDULED")
                .build();
    }

    @Nested
    @DisplayName("Booking Operations")
    class BookingTests {
        @Test
        @DisplayName("Should book appointment and trigger notifications")
        void bookAppointment_Success() {
            AppointmentRequest request = AppointmentRequest.builder()
                    .patientId("PAT-001")
                    .providerId("PRO-999")
                    .serviceType("Consultation")
                    .appointmentDate(LocalDate.now().plusDays(2))
                    .build();

            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            AppointmentResponse response = appointmentService.bookAppointment(request);

            assertNotNull(response);
            assertEquals("AP-101", response.getAppointmentId());
            assertEquals("SCHEDULED", response.getStatus());
            
            // Verify 2 notifications sent: one to Patient, one to Provider
            verify(notificationPublisher, times(2)).publishNotification(any());
            verify(appointmentRepository).save(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("Status Transitions")
    class StatusTests {
        @Test
        @DisplayName("Should cancel appointment successfully")
        void cancelAppointment_Success() {
            when(appointmentRepository.findById("AP-101")).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            AppointmentResponse response = appointmentService.cancelAppointment("AP-101");

            assertEquals("CANCELLED", testAppointment.getStatus());
            verify(notificationPublisher, times(2)).publishNotification(any());
        }

        @Test
        @DisplayName("Should reschedule and update status")
        void rescheduleAppointment_Success() {
            RescheduleRequest request = new RescheduleRequest();
            request.setAppointmentDate(LocalDate.now().plusDays(5));
            request.setStartTime(java.time.LocalTime.parse("11:00:00"));

            when(appointmentRepository.findById("AP-101")).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            AppointmentResponse response = appointmentService.rescheduleAppointment("AP-101", request);

            assertEquals("RESCHEDULED", testAppointment.getStatus());
            assertEquals(request.getAppointmentDate(), testAppointment.getAppointmentDate());
            verify(notificationPublisher, times(1)).publishNotification(any());
        }

        @Test
        @DisplayName("Should mark appointment as COMPLETED")
        void completeAppointment_Success() {
            when(appointmentRepository.findById("AP-101")).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

            AppointmentResponse response = appointmentService.completeAppointment("AP-101");

            assertEquals("COMPLETED", testAppointment.getStatus());
            verify(notificationPublisher, times(1)).publishNotification(any());
        }
    }

    @Nested
    @DisplayName("Exception Handling")
    class ExceptionTests {
        @Test
        @DisplayName("Should throw AppointmentNotFoundException when ID is invalid")
        void getById_Fail_NotFound() {
            when(appointmentRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(AppointmentNotFoundException.class, () -> appointmentService.getById("invalid"));
        }
    }

    @Nested
    @DisplayName("Data Retrieval")
    class RetrievalTests {
        @Test
        @DisplayName("Should return list of appointments for a patient")
        void getByPatient_Success() {
            when(appointmentRepository.findByPatientId("PAT-001")).thenReturn(List.of(testAppointment));
            List<AppointmentResponse> results = appointmentService.getByPatient("PAT-001");
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
        }
    }
}
