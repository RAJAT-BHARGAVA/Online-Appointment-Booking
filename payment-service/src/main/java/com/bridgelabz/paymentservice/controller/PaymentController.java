package com.bridgelabz.paymentservice.controller;

import com.bridgelabz.paymentservice.dto.PaymentRequest;
import com.bridgelabz.paymentservice.dto.PaymentResponse;
import com.bridgelabz.paymentservice.model.PaymentStatus;
import com.bridgelabz.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.processPayment(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PaymentResponse> getByAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(paymentService.getPaymentByAppointment(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaymentResponse>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatient(patientId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<PaymentResponse>> getByProvider(@PathVariable String providerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByProvider(providerId));
    }

    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable String paymentId, @RequestBody(required = false) com.bridgelabz.paymentservice.dto.NotesRequest request) {
        String notes = request != null ? (request.getReason() != null ? request.getReason() : request.getNotes()) : "No notes";
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, notes));
    }

    @PutMapping("/{paymentId}/request-refund")
    public ResponseEntity<PaymentResponse> requestRefund(@PathVariable String paymentId, @RequestBody(required = false) com.bridgelabz.paymentservice.dto.NotesRequest request) {
        String notes = request != null ? (request.getReason() != null ? request.getReason() : request.getNotes()) : "No notes";
        return ResponseEntity.ok(paymentService.requestRefund(paymentId, notes));
    }

    @PutMapping("/{paymentId}/refund/approve")
    public ResponseEntity<PaymentResponse> approveRefund(@PathVariable String paymentId, @RequestBody(required = false) com.bridgelabz.paymentservice.dto.NotesRequest request) {
        String notes = request != null ? (request.getReason() != null ? request.getReason() : request.getNotes()) : "No notes";
        return ResponseEntity.ok(paymentService.approveRefund(paymentId, notes));
    }

    @PutMapping("/{paymentId}/refund/reject")
    public ResponseEntity<PaymentResponse> rejectRefund(@PathVariable String paymentId, @RequestBody(required = false) com.bridgelabz.paymentservice.dto.NotesRequest request) {
        String notes = request != null ? (request.getReason() != null ? request.getReason() : request.getNotes()) : "No notes";
        return ResponseEntity.ok(paymentService.rejectRefund(paymentId, notes));
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentStatus> getStatus(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(paymentId));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable String paymentId, @RequestParam PaymentStatus status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(paymentId, status));
    }

    @GetMapping("/{paymentId}/invoice")
    public ResponseEntity<String> generateInvoice(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.generateInvoice(paymentId));
    }

    @GetMapping({"/revenue", "/revenue/total"})
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(paymentService.getTotalRevenue());
    }

    @GetMapping("/revenue/provider/{providerId}")
    public ResponseEntity<Double> getProviderRevenue(@PathVariable String providerId) {
        return ResponseEntity.ok(paymentService.getTotalRevenue(providerId));
    }

    @PostMapping("/create-order")
    public ResponseEntity<com.bridgelabz.paymentservice.dto.RazorpayOrderResponse> createRazorpayOrder(@Valid @RequestBody com.bridgelabz.paymentservice.dto.RazorpayOrderRequest request) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyPayment(@Valid @RequestBody com.bridgelabz.paymentservice.dto.RazorpayVerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable String paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll() {
        paymentService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}