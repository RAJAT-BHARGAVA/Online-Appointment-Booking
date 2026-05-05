package com.bridgelabz.providerservice.service;

import com.bridgelabz.providerservice.dto.ProviderRequest;
import com.bridgelabz.providerservice.dto.ProviderResponse;

import java.util.List;

public interface ProviderService {

    ProviderResponse registerProvider(ProviderRequest request);

    ProviderResponse getProviderById(String providerId);

    ProviderResponse getProviderByUserId(String userId); // New method

    List<ProviderResponse> getBySpecialization(String specialization);

    List<ProviderResponse> searchProviders(String keyword);

    ProviderResponse updateProvider(String providerId, ProviderRequest request);

    ProviderResponse verifyProvider(String providerId, Boolean isVerified);

    ProviderResponse setAvailability(String providerId, Boolean isAvailable);

    void deleteProvider(String providerId);

    ProviderResponse updateRating(String providerId, Double avgRating);

    List<ProviderResponse> getAllProviders();

    void deleteAll();
}