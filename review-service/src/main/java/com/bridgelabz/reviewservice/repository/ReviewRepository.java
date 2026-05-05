package com.bridgelabz.reviewservice.repository;

import com.bridgelabz.reviewservice.model.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProviderId(String providerId);

    List<Review> findByPatientId(String patientId);

    Optional<Review> findByAppointmentId(String appointmentId);

    List<Review> findByRating(Integer rating);

    long countByProviderId(String providerId);

    boolean existsByAppointmentId(String appointmentId);

    void deleteByReviewId(String reviewId);

    @Aggregation(pipeline = {
            "{ '$match': { 'providerId': ?0 } }",
            "{ '$group': { '_id': null, 'avgRating': { '$avg': '$rating' } } }"
    })
    Double avgRatingByProviderId(String providerId);
}