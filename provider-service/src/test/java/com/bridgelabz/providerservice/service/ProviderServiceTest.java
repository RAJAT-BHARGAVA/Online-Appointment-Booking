package com.bridgelabz.providerservice.service;

import com.bridgelabz.providerservice.dto.ProviderRequest;
import com.bridgelabz.providerservice.dto.ProviderResponse;
import com.bridgelabz.providerservice.exception.ProviderAlreadyExistsException;
import com.bridgelabz.providerservice.exception.ProviderNotFoundException;
import com.bridgelabz.providerservice.model.Provider;
import com.bridgelabz.providerservice.repository.ProviderRepository;
import com.bridgelabz.providerservice.service.impl.ProviderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Provider Service Deep Validation Tests")
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    private Provider testProvider;

    @BeforeEach
    void setUp() {
        testProvider = Provider.builder()
                .providerId("PROV-001")
                .userId("USER-123")
                .fullName("Dr. Rajat Bhargav")
                .specialization("Neurology")
                .isVerified(false)
                .isAvailable(true)
                .build();
    }

    @Nested
    @DisplayName("Provider Registration")
    class RegistrationTests {
        @Test
        @DisplayName("Should register with default name if fullName is missing")
        void registerProvider_DefaultName_Success() {
            ProviderRequest request = ProviderRequest.builder()
                    .userId("NEW-USER")
                    .specialization("General")
                    .build();

            when(providerRepository.findByUserId("NEW-USER")).thenReturn(Optional.empty());
            when(providerRepository.save(any(Provider.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ProviderResponse response = providerService.registerProvider(request);

            assertEquals("Rajat Bhargav", response.getFullName()); // Fallback logic
            verify(providerRepository).save(any(Provider.class));
        }

        @Test
        @DisplayName("Should fail if provider profile already exists")
        void registerProvider_Fail_AlreadyExists() {
            ProviderRequest request = ProviderRequest.builder().userId("USER-123").build();
            when(providerRepository.findByUserId("USER-123")).thenReturn(Optional.of(testProvider));

            assertThrows(ProviderAlreadyExistsException.class, () -> providerService.registerProvider(request));
        }
    }

    @Nested
    @DisplayName("Auto-Correction Logic")
    class CorrectionTests {
        @Test
        @DisplayName("Should fix legacy 'Provider-' names to 'Rajat Bhargav' on retrieval")
        void getProviderByUserId_AutoFixName() {
            testProvider.setFullName("Provider-12345");
            when(providerRepository.findByUserId("USER-123")).thenReturn(Optional.of(testProvider));

            ProviderResponse response = providerService.getProviderByUserId("USER-123");

            assertEquals("Rajat Bhargav", response.getFullName());
            verify(providerRepository).save(testProvider); // Should save the fixed name
        }
    }

    @Nested
    @DisplayName("Search & Filters")
    class SearchTests {
        @Test
        @DisplayName("Should search by specialization or name keyword")
        void searchProviders_Success() {
            when(providerRepository.findByFullNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase("neuro", "neuro"))
                    .thenReturn(List.of(testProvider));

            List<ProviderResponse> results = providerService.searchProviders("neuro");

            assertEquals(1, results.size());
            assertEquals("Neurology", results.get(0).getSpecialization());
        }
    }

    @Nested
    @DisplayName("Management Operations")
    class ManagementTests {
        @Test
        @DisplayName("Should update verification status")
        void verifyProvider_Success() {
            when(providerRepository.findById("PROV-001")).thenReturn(Optional.of(testProvider));
            when(providerRepository.save(testProvider)).thenReturn(testProvider);

            ProviderResponse response = providerService.verifyProvider("PROV-001", true);

            assertTrue(response.getIsVerified());
            verify(providerRepository).save(testProvider);
        }

        @Test
        @DisplayName("Should throw exception if provider ID is invalid")
        void getProviderById_Fail_NotFound() {
            when(providerRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(ProviderNotFoundException.class, () -> providerService.getProviderById("invalid"));
        }
    }
}
