package com.bridgelabz.paymentservice.service;

import com.bridgelabz.paymentservice.dto.PaymentRequest;
import com.bridgelabz.paymentservice.dto.PaymentResponse;
import com.bridgelabz.paymentservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentByAppointment(String appointmentId);

    List<PaymentResponse> getPaymentsByPatient(String patientId);

    List<PaymentResponse> getPaymentHistory(LocalDateTime start, LocalDateTime end);

    PaymentResponse refundPayment(String paymentId, String notes);

    PaymentResponse requestRefund(String paymentId, String notes);

    PaymentResponse approveRefund(String paymentId, String notes);

    PaymentResponse rejectRefund(String paymentId, String notes);

    PaymentStatus getPaymentStatus(String paymentId);

    PaymentResponse updatePaymentStatus(String paymentId, PaymentStatus status);

    String generateInvoice(String paymentId);

    Double getTotalRevenue();
    Double getTotalRevenue(String providerId);

    List<PaymentResponse> getPaymentsByProvider(String providerId);

    List<PaymentResponse> getAllPayments();

    void deletePayment(String paymentId);
    void deleteAll();

    // Razorpay Integration
    com.bridgelabz.paymentservice.dto.RazorpayOrderResponse createRazorpayOrder(com.bridgelabz.paymentservice.dto.RazorpayOrderRequest request);
    boolean verifyPayment(com.bridgelabz.paymentservice.dto.RazorpayVerifyRequest request);
}