package com.bridgelabz.providerservice.repository;

import com.bridgelabz.providerservice.model.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {

    Optional<Provider> findByUserId(String userId); // Changed from Long to String

    List<Provider> findBySpecializationIgnoreCase(String specialization);

    List<Provider> findByIsVerified(Boolean isVerified);

    List<Provider> findByIsAvailable(Boolean isAvailable);

    List<Provider> findByClinicAddressContainingIgnoreCase(String clinicAddress);

    long countBySpecializationIgnoreCase(String specialization);

    List<Provider> findByFullNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
            String fullName,
            String specialization
    );
}