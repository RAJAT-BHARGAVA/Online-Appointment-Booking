package com.bridgelabz.reviewservice.service;

import com.bridgelabz.reviewservice.dto.ReviewRequest;
import com.bridgelabz.reviewservice.dto.ReviewResponse;
import com.bridgelabz.reviewservice.dto.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(ReviewRequest request);

    List<ReviewResponse> getByProvider(String providerId);

    List<ReviewResponse> getByPatient(String patientId);

    ReviewResponse getByAppointment(String appointmentId);

    ReviewResponse updateReview(String reviewId, ReviewUpdateRequest request);

    void deleteReview(String reviewId);

    Double getAvgRating(String providerId);

    long getReviewCount(String providerId);

    List<ReviewResponse> getAllReviews();

    void deleteAll();
}