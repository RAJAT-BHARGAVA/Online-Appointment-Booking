package com.bridgelabz.providerservice.controller;

import com.bridgelabz.providerservice.dto.*;
import com.bridgelabz.providerservice.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<ProviderResponse> registerProvider(@Valid @RequestBody ProviderRequest request) {
        return new ResponseEntity<>(providerService.registerProvider(request), HttpStatus.CREATED);
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderResponse> getProviderById(@PathVariable String providerId) {
        return ResponseEntity.ok(providerService.getProviderById(providerId));
    }

    // New Endpoint matching frontend: api.get(`/providers/user/${userId}`)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProviderResponse> getProviderByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(providerService.getProviderByUserId(userId));
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<ProviderResponse>> getBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(providerService.getBySpecialization(specialization));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProviderResponse>> searchProviders(@RequestParam String keyword) {
        return ResponseEntity.ok(providerService.searchProviders(keyword));
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @PutMapping("/{providerId}")
    public ResponseEntity<ProviderResponse> updateProvider(@PathVariable String providerId,
                                                           @Valid @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(providerId, request));
    }

    @PutMapping("/{providerId}/verify")
    public ResponseEntity<ProviderResponse> verifyProvider(@PathVariable String providerId,
                                                           @Valid @RequestBody VerificationRequest request) {
        return ResponseEntity.ok(providerService.verifyProvider(providerId, request.getIsVerified()));
    }

    @PutMapping("/{providerId}/availability")
    public ResponseEntity<ProviderResponse> setAvailability(@PathVariable String providerId,
                                                            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(providerService.setAvailability(providerId, request.getIsAvailable()));
    }

    @PutMapping("/{providerId}/rating")
    public ResponseEntity<ProviderResponse> updateRating(@PathVariable String providerId,
                                                         @Valid @RequestBody RatingUpdateRequest request) {
        return ResponseEntity.ok(providerService.updateRating(providerId, request.getAvgRating()));
    }

    @DeleteMapping("/{providerId}")
    public ResponseEntity<Void> deleteProvider(@PathVariable String providerId) {
        providerService.deleteProvider(providerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAll() {
        providerService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}