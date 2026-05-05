package com.bridgelabz.paymentservice.service;

import com.bridgelabz.paymentservice.dto.PaymentRequest;
import com.bridgelabz.paymentservice.dto.PaymentResponse;
import com.bridgelabz.paymentservice.exception.InvalidPaymentOperationException;
import com.bridgelabz.paymentservice.exception.PaymentNotFoundException;
import com.bridgelabz.paymentservice.model.Payment;
import com.bridgelabz.paymentservice.model.PaymentMode;
import com.bridgelabz.paymentservice.model.PaymentStatus;
import com.bridgelabz.paymentservice.repository.PaymentRepository;
import com.bridgelabz.paymentservice.publisher.NotificationPublisher;
import com.bridgelabz.paymentservice.service.impl.PaymentServiceImpl;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Comprehensive Deep Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private RazorpayClient razorpayClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testPayment = Payment.builder()
                .paymentId("PAY-123")
                .appointmentId("AP-101")
                .patientId("PAT-1")
                .providerId("PRO-1")
                .amount(1000.0)
                .status(PaymentStatus.PAID)
                .currency("INR")
                .mode(PaymentMode.Online)
                .build();
    }

    @Nested
    @DisplayName("Payment Processing")
    class ProcessingTests {
        @Test
        @DisplayName("Should process payment and notify 3 parties (Patient, Provider, Admin)")
        void processPayment_Success() {
            PaymentRequest request = PaymentRequest.builder()
                    .appointmentId("AP-101")
                    .amount(1000.0)
                    .mode(PaymentMode.Online)
                    .build();

            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

            PaymentResponse response = paymentService.processPayment(request);

            assertNotNull(response);
            assertEquals(PaymentStatus.PAID, response.getStatus());
            // Verify 3 notifications are published
            verify(notificationPublisher, times(3)).publishNotification(any());
        }

        @Test
        @DisplayName("Should set status to PENDING for Pay-at-Clinic mode")
        void processPayment_ClinicMode() {
            Payment clinicPayment = Payment.builder().status(PaymentStatus.PENDING).build();
            PaymentRequest request = PaymentRequest.builder()
                    .mode(PaymentMode.PAY_AT_CLINIC)
                    .build();

            when(paymentRepository.save(any(Payment.class))).thenReturn(clinicPayment);

            PaymentResponse response = paymentService.processPayment(request);
            assertEquals(PaymentStatus.PENDING, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Refund Lifecycle")
    class RefundTests {
        @Test
        @DisplayName("Should request refund successfully")
        void requestRefund_Success() {
            when(paymentRepository.findById("PAY-123")).thenReturn(Optional.of(testPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

            PaymentResponse response = paymentService.requestRefund("PAY-123", "Reason: Duplicate");

            assertEquals(PaymentStatus.REFUND_REQUESTED, testPayment.getStatus());
            verify(notificationPublisher, atLeastOnce()).publishNotification(any());
        }

        @Test
        @DisplayName("Should approve refund and set status to REFUNDED")
        void approveRefund_Success() {
            testPayment.setStatus(PaymentStatus.REFUND_REQUESTED);
            when(paymentRepository.findById("PAY-123")).thenReturn(Optional.of(testPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

            PaymentResponse response = paymentService.approveRefund("PAY-123", "Approved by Admin");

            assertEquals(PaymentStatus.REFUNDED, testPayment.getStatus());
        }

        @Test
        @DisplayName("Should fail to refund a payment that was never PAID")
        void refundPayment_Fail_InvalidStatus() {
            testPayment.setStatus(PaymentStatus.PENDING);
            when(paymentRepository.findById("PAY-123")).thenReturn(Optional.of(testPayment));

            assertThrows(InvalidPaymentOperationException.class, 
                () -> paymentService.refundPayment("PAY-123", "Notes"));
        }
    }

    @Nested
    @DisplayName("Revenue & Queries")
    class QueryTests {
        @Test
        @DisplayName("Should throw exception if payment not found")
        void getPaymentByAppointment_Fail_NotFound() {
            when(paymentRepository.findByAppointmentId("invalid")).thenReturn(Optional.empty());
            assertThrows(PaymentNotFoundException.class, 
                () -> paymentService.getPaymentByAppointment("invalid"));
        }
    }
}
