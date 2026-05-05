package com.bridgelabz.reviewservice.service;

import com.bridgelabz.reviewservice.dto.ReviewRequest;
import com.bridgelabz.reviewservice.dto.ReviewResponse;
import com.bridgelabz.reviewservice.dto.ReviewUpdateRequest;
import com.bridgelabz.reviewservice.exception.DuplicateReviewException;
import com.bridgelabz.reviewservice.exception.ReviewNotFoundException;
import com.bridgelabz.reviewservice.model.Review;
import com.bridgelabz.reviewservice.repository.ReviewRepository;
import com.bridgelabz.reviewservice.service.impl.ReviewServiceImpl;
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
@DisplayName("Review Service Comprehensive Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = Review.builder()
                .reviewId("REV-1")
                .appointmentId("AP-101")
                .patientId("PAT-1")
                .providerId("PRO-1")
                .rating(5)
                .comment("Excellent consultation")
                .isAnonymous(false)
                .build();
    }

    @Nested
    @DisplayName("Review Submission")
    class SubmissionTests {
        @Test
        @DisplayName("Should add review when it's the first one for the appointment")
        void addReview_Success() {
            ReviewRequest request = ReviewRequest.builder()
                    .appointmentId("AP-101")
                    .rating(5)
                    .build();

            when(reviewRepository.existsByAppointmentId("AP-101")).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            ReviewResponse response = reviewService.addReview(request);

            assertNotNull(response);
            assertEquals(5, response.getRating());
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("Should fail if review for this appointment already exists")
        void addReview_Fail_Duplicate() {
            ReviewRequest request = ReviewRequest.builder().appointmentId("AP-101").build();
            when(reviewRepository.existsByAppointmentId("AP-101")).thenReturn(true);

            assertThrows(DuplicateReviewException.class, () -> reviewService.addReview(request));
        }
    }

    @Nested
    @DisplayName("Rating & Statistics")
    class StatsTests {
        @Test
        @DisplayName("Should return average rating for provider")
        void getAvgRating_Success() {
            when(reviewRepository.avgRatingByProviderId("PRO-1")).thenReturn(4.8);
            Double avg = reviewService.getAvgRating("PRO-1");
            assertEquals(Double.valueOf(4.8), avg);
        }

        @Test
        @DisplayName("Should return 0.0 if no reviews found for provider")
        void getAvgRating_Empty_Success() {
            when(reviewRepository.avgRatingByProviderId("PRO-1")).thenReturn(null);
            Double avg = reviewService.getAvgRating("PRO-1");
            assertEquals(Double.valueOf(0.0), avg);
        }

        @Test
        @DisplayName("Should return accurate review count")
        void getReviewCount_Success() {
            when(reviewRepository.countByProviderId("PRO-1")).thenReturn(15L);
            long count = reviewService.getReviewCount("PRO-1");
            assertEquals(15L, count);
        }
    }

    @Nested
    @DisplayName("Modification Flow")
    class ModificationTests {
        @Test
        @DisplayName("Should update review comment and rating")
        void updateReview_Success() {
            ReviewUpdateRequest request = new ReviewUpdateRequest();
            request.setRating(5);
            request.setComment("Updated: Excellent!");

            when(reviewRepository.findById("REV-1")).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(testReview)).thenReturn(testReview);

            ReviewResponse response = reviewService.updateReview("REV-1", request);

            assertEquals(5, testReview.getRating());
            assertEquals("Updated: Excellent!", testReview.getComment());
        }

        @Test
        @DisplayName("Should throw exception for invalid review ID")
        void deleteReview_Fail_NotFound() {
            when(reviewRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview("invalid"));
        }
    }
}
