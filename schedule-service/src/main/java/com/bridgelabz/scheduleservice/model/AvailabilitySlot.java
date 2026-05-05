package com.bridgelabz.scheduleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "availability_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySlot {

    @Id
    private String slotId;

    private String providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;

    @Builder.Default
    private Boolean isBooked = false;

    @Builder.Default
    private Boolean isBlocked = false;

    private String recurrence;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}