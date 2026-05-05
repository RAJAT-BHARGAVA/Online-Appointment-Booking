package com.bridgelabz.paymentservice.service.impl;

import com.bridgelabz.paymentservice.dto.PaymentRequest;
import com.bridgelabz.paymentservice.dto.PaymentResponse;
import com.bridgelabz.paymentservice.exception.InvalidPaymentOperationException;
import com.bridgelabz.paymentservice.exception.PaymentNotFoundException;
import com.bridgelabz.paymentservice.model.Payment;
import com.bridgelabz.paymentservice.model.PaymentMode;
import com.bridgelabz.paymentservice.model.PaymentStatus;
import com.bridgelabz.paymentservice.repository.PaymentRepository;
import com.bridgelabz.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final com.bridgelabz.paymentservice.publisher.NotificationPublisher notificationPublisher;
    private final com.razorpay.RazorpayClient razorpayClient;

    @org.springframework.beans.factory.annotation.Value("${razorpay.api.secret}")
    private String apiSecret;

    @Override
    public com.bridgelabz.paymentservice.dto.RazorpayOrderResponse createRazorpayOrder(com.bridgelabz.paymentservice.dto.RazorpayOrderRequest request) {
        try {
            org.json.JSONObject orderRequest = new org.json.JSONObject();
            orderRequest.put("amount", (int) (request.getAmount() * 100)); // amount in paise
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", request.getAppointmentId());
            
            com.razorpay.Order order = razorpayClient.orders.create(orderRequest);
            return com.bridgelabz.paymentservice.dto.RazorpayOrderResponse.builder()
                    .orderId(order.get("id"))
                    .build();
        } catch (com.razorpay.RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(com.bridgelabz.paymentservice.dto.RazorpayVerifyRequest request) {
        try {
            org.json.JSONObject options = new org.json.JSONObject();
            options.put("razorpay_order_id", request.getOrderId());
            options.put("razorpay_payment_id", request.getPaymentId());
            options.put("razorpay_signature", request.getSignature());

            return com.razorpay.Utils.verifyPaymentSignature(options, apiSecret);
        } catch (com.razorpay.RazorpayException e) {
            return false;
        }
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Payment payment = Payment.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .mode(request.getMode())
                .transactionId("TXN-" + System.currentTimeMillis())
                .currency(request.getCurrency())
                .notes(request.getNotes())
                .build();

        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());
        } else if (request.getMode() == PaymentMode.PAY_AT_CLINIC || request.getMode() == PaymentMode.CASH) {
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            payment.setStatus(PaymentStatus.PAID);
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getStatus() == PaymentStatus.PAID) {
            try {
                // Notify Patient
                notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                        .recipientId(savedPayment.getPatientId())
                        .type("PAYMENT")
                        .title("Payment Successful")
                        .message("Payment of ₹" + savedPayment.getAmount() + " received for appointment #" + savedPayment.getAppointmentId())
                        .channel("EMAIL")
                        .relatedId(savedPayment.getAppointmentId())
                        .relatedType("APPOINTMENT")
                        .build());

                // Notify Provider
                notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                        .recipientId(savedPayment.getProviderId())
                        .type("PAYMENT")
                        .title("Payment Received")
                        .message("You received a payment of ₹" + savedPayment.getAmount() + " for appointment #" + savedPayment.getAppointmentId())
                        .channel("APP")
                        .relatedId(savedPayment.getAppointmentId())
                        .relatedType("APPOINTMENT")
                        .build());

                // Notify Admin
                notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                        .recipientId("admin")
                        .type("PAYMENT")
                        .title("New Payment Received")
                        .message("Payment of ₹" + savedPayment.getAmount() + " received for appointment #" + savedPayment.getAppointmentId())
                        .channel("APP")
                        .relatedId(savedPayment.getAppointmentId())
                        .relatedType("APPOINTMENT")
                        .build());
            } catch (Exception e) {
                // Log the error but don't fail the payment process
                System.err.println("Failed to send notifications: " + e.getMessage());
            }
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByAppointment(String appointmentId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for appointmentId: " + appointmentId));

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByPatient(String patientId) {
        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentHistory(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaidAtBetween(start, end)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PaymentResponse refundPayment(String paymentId, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PAID && payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
            throw new InvalidPaymentOperationException("Only PAID or REFUND_REQUESTED payments can be refunded");
        }

        // Actually set status to REFUNDED and record the timestamp
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setNotes(notes != null ? notes : "Refund processed");
        Payment savedPayment = paymentRepository.save(payment);

        try {
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId(savedPayment.getPatientId())
                    .type("PAYMENT")
                    .title("Refund Processed")
                    .message("Your refund of ₹" + savedPayment.getAmount() + " has been processed successfully.")
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send refund notification: " + e.getMessage());
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse requestRefund(String paymentId, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new InvalidPaymentOperationException("Only PAID payments can request a refund");
        }

        payment.setStatus(PaymentStatus.REFUND_REQUESTED);
        payment.setNotes(notes);
        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Notify Patient that refund request is submitted
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId(savedPayment.getPatientId())
                    .type("PAYMENT")
                    .title("Refund Requested")
                    .message("Your refund request for ₹" + savedPayment.getAmount() + " has been submitted and is pending review.")
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());

            // Notify Provider that a refund is requested
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId(savedPayment.getProviderId())
                    .type("PAYMENT")
                    .title("Refund Request Received")
                    .message("A patient has requested a refund for appointment #" + savedPayment.getAppointmentId() + ". Please review the request.")
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());

            // Notify Admin
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId("admin")
                    .type("PAYMENT")
                    .title("New Refund Request")
                    .message("New refund request received for amount ₹" + savedPayment.getAmount() + ". Needs verification.")
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send refund request notifications: " + e.getMessage());
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse approveRefund(String paymentId, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
            throw new InvalidPaymentOperationException("Only REFUND_REQUESTED payments can be approved");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setNotes("Refund Approved: " + (notes != null ? notes : ""));
        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Notify Patient: refund approved
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId(savedPayment.getPatientId())
                    .type("PAYMENT")
                    .title("Refund Approved")
                    .message("Your refund of ₹" + savedPayment.getAmount() + " has been approved and will be processed shortly.")
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());

            // Notify Admin
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId("admin")
                    .type("PAYMENT")
                    .title("Refund Approved")
                    .message("Refund of ₹" + savedPayment.getAmount() + " approved for appointment #" + savedPayment.getAppointmentId())
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send refund approval notifications: " + e.getMessage());
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse rejectRefund(String paymentId, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.REFUND_REQUESTED) {
            throw new InvalidPaymentOperationException("Only REFUND_REQUESTED payments can be rejected");
        }

        payment.setStatus(PaymentStatus.PAID); // Revert to PAID
        payment.setNotes("Refund Rejected: " + notes);
        Payment savedPayment = paymentRepository.save(payment);

        try {
            // Notify Patient that refund request was rejected
            notificationPublisher.publishNotification(com.bridgelabz.paymentservice.dto.NotificationRequest.builder()
                    .recipientId(savedPayment.getPatientId())
                    .type("PAYMENT")
                    .title("Refund Request Rejected")
                    .message("Your refund request for ₹" + savedPayment.getAmount() + " has been rejected. Reason: " + notes)
                    .channel("APP")
                    .relatedId(savedPayment.getAppointmentId())
                    .relatedType("APPOINTMENT")
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send refund rejection notification: " + e.getMessage());
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        return payment.getStatus();
    }

    @Override
    public PaymentResponse updatePaymentStatus(String paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        // Removed the restriction that prevented changing REFUNDED status
        // to allow admins to "Cancel Refund" and revert back to PAID.
        if (payment.getStatus() == PaymentStatus.REFUNDED && status != PaymentStatus.PAID) {
            throw new InvalidPaymentOperationException("Refunded payment status can only be reverted to PAID.");
        }

        payment.setStatus(status);

        if (status == PaymentStatus.PAID && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        if (status == PaymentStatus.REFUNDED && payment.getRefundedAt() == null) {
            payment.setRefundedAt(LocalDateTime.now());
        }

        return mapToResponse(paymentRepository.save(payment));
    }

    @Override
    public String generateInvoice(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        return "Invoice | Payment ID: " + payment.getPaymentId()
                + " | Appointment ID: " + payment.getAppointmentId()
                + " | Patient ID: " + payment.getPatientId()
                + " | Amount: " + payment.getAmount() + " " + payment.getCurrency()
                + " | Mode: " + payment.getMode()
                + " | Status: " + payment.getStatus()
                + " | Transaction ID: " + payment.getTransactionId();
    }

    @Override
    public Double getTotalRevenue() {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public Double getTotalRevenue(String providerId) {
        return paymentRepository.findByProviderId(providerId)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public List<PaymentResponse> getPaymentsByProvider(String providerId) {
        return paymentRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deletePayment(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public void deleteAll() {
        paymentRepository.deleteAll();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .appointmentId(payment.getAppointmentId())
                .patientId(payment.getPatientId())
                .providerId(payment.getProviderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .mode(payment.getMode())
                .transactionId(payment.getTransactionId())
                .currency(payment.getCurrency())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .notes(payment.getNotes())
                .build();
    }
}