package com.bridgelabz.reviewservice.service.impl;

import com.bridgelabz.reviewservice.dto.ReviewRequest;
import com.bridgelabz.reviewservice.dto.ReviewResponse;
import com.bridgelabz.reviewservice.dto.ReviewUpdateRequest;
import com.bridgelabz.reviewservice.exception.DuplicateReviewException;
import com.bridgelabz.reviewservice.exception.ReviewNotFoundException;
import com.bridgelabz.reviewservice.model.Review;
import com.bridgelabz.reviewservice.repository.ReviewRepository;
import com.bridgelabz.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse addReview(ReviewRequest request) {
        if (reviewRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new DuplicateReviewException("Review already exists for appointmentId: " + request.getAppointmentId());
        }

        Review review = Review.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .providerId(request.getProviderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .isAnonymous(request.getIsAnonymous())
                .build();

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getByProvider(String providerId) {
        return reviewRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> getByPatient(String patientId) {
        return reviewRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReviewResponse getByAppointment(String appointmentId) {
        Review review = reviewRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found for appointmentId: " + appointmentId));

        return mapToResponse(review);
    }

    @Override
    public ReviewResponse updateReview(String reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsAnonymous(request.getIsAnonymous());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
    }

    @Override
    public Double getAvgRating(String providerId) {
        Double avg = reviewRepository.avgRatingByProviderId(providerId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getReviewCount(String providerId) {
        return reviewRepository.countByProviderId(providerId);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAll() {
        reviewRepository.deleteAll();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .appointmentId(review.getAppointmentId())
                .patientId(review.getPatientId())
                .providerId(review.getProviderId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .isVerified(review.getIsVerified())
                .isAnonymous(review.getIsAnonymous())
                .build();
    }
}