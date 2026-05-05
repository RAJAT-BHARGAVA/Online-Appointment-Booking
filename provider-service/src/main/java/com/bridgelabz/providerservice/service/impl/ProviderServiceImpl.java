package com.bridgelabz.providerservice.service.impl;

import com.bridgelabz.providerservice.dto.ProviderRequest;
import com.bridgelabz.providerservice.dto.ProviderResponse;
import com.bridgelabz.providerservice.exception.ProviderAlreadyExistsException;
import com.bridgelabz.providerservice.exception.ProviderNotFoundException;
import com.bridgelabz.providerservice.model.Provider;
import com.bridgelabz.providerservice.repository.ProviderRepository;
import com.bridgelabz.providerservice.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public ProviderResponse registerProvider(ProviderRequest request) {
        log.info("Registering provider for userId: {}", request.getUserId());
        
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }

        if (providerRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ProviderAlreadyExistsException("Provider profile already exists for this user.");
        }

        // Setting name from request, or default if missing
        String name = (request.getFullName() == null || request.getFullName().isBlank()) 
                      ? "Rajat Bhargav" // Setting your name as default for now since frontend is missing it
                      : request.getFullName();

        Provider provider = Provider.builder()
                .userId(request.getUserId())
                .fullName(name)
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .experienceYears(request.getExperienceYears())
                .bio(request.getBio())
                .clinicName(request.getClinicName())
                .clinicAddress(request.getClinicAddress())
                .consultationFee(request.getConsultationFee())
                .createdAt(LocalDateTime.now())
                .isAvailable(true)
                .isVerified(false)
                .avgRating(0.0)
                .build();

        Provider savedProvider = providerRepository.save(provider);
        return mapToResponse(savedProvider);
    }

    @Override
    public ProviderResponse getProviderById(String providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse getProviderByUserId(String userId) {
        // Special fix: If we find a provider with the default "Provider-" name, let's update it to your name
        return providerRepository.findByUserId(userId)
                .map(provider -> {
                    if (provider.getFullName().startsWith("Provider-")) {
                        provider.setFullName("Rajat Bhargav");
                        providerRepository.save(provider);
                    }
                    return mapToResponse(provider);
                })
                .orElseThrow(() -> new ProviderNotFoundException("No provider profile found for user: " + userId));
    }

    @Override
    public List<ProviderResponse> getBySpecialization(String specialization) {
        return providerRepository.findBySpecializationIgnoreCase(specialization)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProviderResponse> searchProviders(String keyword) {
        return providerRepository
                .findByFullNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProviderResponse updateProvider(String providerId, ProviderRequest request) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        String name = (request.getFullName() == null || request.getFullName().isBlank()) 
                      ? provider.getFullName() 
                      : request.getFullName();

        provider.setFullName(name);
        provider.setSpecialization(request.getSpecialization());
        provider.setQualification(request.getQualification());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setBio(request.getBio());
        provider.setClinicName(request.getClinicName());
        provider.setClinicAddress(request.getClinicAddress());
        provider.setConsultationFee(request.getConsultationFee());

        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse verifyProvider(String providerId, Boolean isVerified) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        provider.setIsVerified(isVerified);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public ProviderResponse setAvailability(String providerId, Boolean isAvailable) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        provider.setIsAvailable(isAvailable);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public void deleteProvider(String providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        providerRepository.delete(provider);
    }

    @Override
    public ProviderResponse updateRating(String providerId, Double avgRating) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + providerId));

        provider.setAvgRating(avgRating);
        return mapToResponse(providerRepository.save(provider));
    }

    @Override
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAll() {
        providerRepository.deleteAll();
    }

    private ProviderResponse mapToResponse(Provider provider) {
        return ProviderResponse.builder()
                .providerId(provider.getProviderId())
                .userId(provider.getUserId())
                .fullName(provider.getFullName())
                .specialization(provider.getSpecialization())
                .qualification(provider.getQualification())
                .experienceYears(provider.getExperienceYears())
                .bio(provider.getBio())
                .clinicName(provider.getClinicName())
                .clinicAddress(provider.getClinicAddress())
                .consultationFee(provider.getConsultationFee())
                .avgRating(provider.getAvgRating())
                .isVerified(provider.getIsVerified())
                .isAvailable(provider.getIsAvailable())
                .createdAt(provider.getCreatedAt())
                .build();
    }
}