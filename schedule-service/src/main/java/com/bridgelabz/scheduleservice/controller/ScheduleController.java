package com.bridgelabz.scheduleservice.controller;

import com.bridgelabz.scheduleservice.dto.BlockSlotRequest;
import com.bridgelabz.scheduleservice.dto.RecurringSlotRequest;
import com.bridgelabz.scheduleservice.dto.SlotRequest;
import com.bridgelabz.scheduleservice.dto.SlotResponse;
import com.bridgelabz.scheduleservice.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<SlotResponse> addSlot(@Valid @RequestBody SlotRequest request) {
        return new ResponseEntity<>(scheduleService.addSlot(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        return ResponseEntity.ok(scheduleService.getAllSlots());
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SlotResponse>> addBulkSlots(@Valid @RequestBody List<SlotRequest> requests) {
        return new ResponseEntity<>(scheduleService.addBulkSlots(requests), HttpStatus.CREATED);
    }

    @PostMapping("/generateRecurring")
    public ResponseEntity<List<SlotResponse>> generateRecurringSlots(@Valid @RequestBody RecurringSlotRequest request) {
        return new ResponseEntity<>(scheduleService.generateRecurringSlots(request), HttpStatus.CREATED);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<SlotResponse>> getSlotsByProvider(@PathVariable String providerId) {
        return ResponseEntity.ok(scheduleService.getSlotsByProvider(providerId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(@RequestParam String providerId,
                                                                @RequestParam LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(providerId, date));
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<SlotResponse> getSlotById(@PathVariable String slotId) {
        return ResponseEntity.ok(scheduleService.getSlotById(slotId));
    }

    @PutMapping("/{slotId}/block")
    public ResponseEntity<SlotResponse> blockSlot(@PathVariable String slotId,
                                                  @Valid @RequestBody BlockSlotRequest request) {
        return ResponseEntity.ok(scheduleService.blockSlot(slotId, request.getIsBlocked()));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<SlotResponse> updateSlot(@PathVariable String slotId,
                                                   @Valid @RequestBody SlotRequest request) {
        return ResponseEntity.ok(scheduleService.updateSlot(slotId, request));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String slotId) {
        scheduleService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}