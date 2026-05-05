package com.bridgelabz.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private String reviewId;
    private String appointmentId;
    private String patientId;
    private String providerId;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    private Boolean isVerified;
    private Boolean isAnonymous;
}