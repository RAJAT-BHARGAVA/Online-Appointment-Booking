package com.bridgelabz.scheduleservice.service;

import com.bridgelabz.scheduleservice.dto.*;
import com.bridgelabz.scheduleservice.exception.SlotNotFoundException;
import com.bridgelabz.scheduleservice.model.AvailabilitySlot;
import com.bridgelabz.scheduleservice.repository.SlotRepository;
import com.bridgelabz.scheduleservice.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Schedule Service Deep Logic Tests")
class ScheduleServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private AvailabilitySlot testSlot;

    @BeforeEach
    void setUp() {
        testSlot = AvailabilitySlot.builder()
                .slotId("SL-100")
                .providerId("PRO-1")
                .date(LocalDate.now().plusDays(1))
                .startTime(java.time.LocalTime.parse("09:00:00"))
                .endTime(java.time.LocalTime.parse("09:30:00"))
                .isBooked(false)
                .isBlocked(false)
                .build();
    }

    @Nested
    @DisplayName("Slot Management")
    class SlotOpsTests {
        @Test
        @DisplayName("Should add a new slot successfully")
        void addSlot_Success() {
            SlotRequest request = SlotRequest.builder()
                    .providerId("PRO-1")
                    .date(LocalDate.now().plusDays(1))
                    .build();

            when(slotRepository.save(any(AvailabilitySlot.class))).thenReturn(testSlot);

            SlotResponse response = scheduleService.addSlot(request);

            assertNotNull(response);
            assertEquals("SL-100", response.getSlotId());
            verify(slotRepository).save(any(AvailabilitySlot.class));
        }

        @Test
        @DisplayName("Should block/unblock a slot successfully")
        void blockSlot_Success() {
            when(slotRepository.findById("SL-100")).thenReturn(Optional.of(testSlot));
            when(slotRepository.save(testSlot)).thenReturn(testSlot);

            SlotResponse response = scheduleService.blockSlot("SL-100", true);

            assertTrue(testSlot.getIsBlocked());
            verify(slotRepository).save(testSlot);
        }
    }

    @Nested
    @DisplayName("Availability Logic")
    class AvailabilityTests {
        @Test
        @DisplayName("Should fetch only available slots (not booked, not blocked)")
        void getAvailableSlots_Success() {
            LocalDate date = LocalDate.now().plusDays(1);
            when(slotRepository.findByProviderIdAndDateAndIsBookedFalseAndIsBlockedFalse("PRO-1", date))
                    .thenReturn(List.of(testSlot));

            List<SlotResponse> results = scheduleService.getAvailableSlots("PRO-1", date);

            assertEquals(1, results.size());
            assertFalse(results.get(0).getIsBooked());
            assertFalse(results.get(0).getIsBlocked());
        }
    }

    @Nested
    @DisplayName("Recurring Logic")
    class RecurringTests {
        @Test
        @DisplayName("Should generate multiple slots for a given date range")
        void generateRecurringSlots_Success() {
            RecurringSlotRequest request = new RecurringSlotRequest();
            request.setProviderId("PRO-1");
            request.setStartDate(LocalDate.now());
            request.setEndDate(LocalDate.now().plusDays(4)); // 5 days total
            request.setStartTime(java.time.LocalTime.parse("10:00:00"));
            request.setEndTime(java.time.LocalTime.parse("10:30:00"));

            when(slotRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

            List<SlotResponse> results = scheduleService.generateRecurringSlots(request);

            assertEquals(5, results.size());
            verify(slotRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("Error Scenarios")
    class ErrorTests {
        @Test
        @DisplayName("Should throw SlotNotFoundException for invalid ID")
        void getSlotById_Fail_NotFound() {
            when(slotRepository.findById("invalid")).thenReturn(Optional.empty());
            assertThrows(SlotNotFoundException.class, () -> scheduleService.getSlotById("invalid"));
        }
    }
}
