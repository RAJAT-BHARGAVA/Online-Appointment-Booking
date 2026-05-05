package com.bridgelabz.scheduleservice.service;

import com.bridgelabz.scheduleservice.dto.RecurringSlotRequest;
import com.bridgelabz.scheduleservice.dto.SlotRequest;
import com.bridgelabz.scheduleservice.dto.SlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    SlotResponse addSlot(SlotRequest request);

    List<SlotResponse> addBulkSlots(List<SlotRequest> requests);

    List<SlotResponse> getSlotsByProvider(String providerId);

    List<SlotResponse> getAvailableSlots(String providerId, LocalDate date);

    SlotResponse getSlotById(String slotId);

    SlotResponse blockSlot(String slotId, Boolean isBlocked);

    SlotResponse updateSlot(String slotId, SlotRequest request);

    void deleteSlot(String slotId);

    List<SlotResponse> generateRecurringSlots(RecurringSlotRequest request);

    List<SlotResponse> getAllSlots(); // Added missing method
}