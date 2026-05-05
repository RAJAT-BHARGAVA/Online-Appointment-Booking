package com.bridgelabz.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    private String reviewId;

    private String appointmentId;
    private String patientId;
    private String providerId;
    private Integer rating;
    private String comment;

    @Builder.Default
    private LocalDateTime reviewDate = LocalDateTime.now();

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isAnonymous = false;
}