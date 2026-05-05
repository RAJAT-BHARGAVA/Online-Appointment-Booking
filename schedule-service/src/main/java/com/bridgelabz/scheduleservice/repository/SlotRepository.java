package com.bridgelabz.scheduleservice.repository;

import com.bridgelabz.scheduleservice.model.AvailabilitySlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SlotRepository extends MongoRepository<AvailabilitySlot, String> {

    List<AvailabilitySlot> findByProviderId(String providerId);

    List<AvailabilitySlot> findByProviderIdAndDate(String providerId, LocalDate date);

    List<AvailabilitySlot> findByProviderIdAndDateAndIsBookedFalseAndIsBlockedFalse(String providerId, LocalDate date);

    List<AvailabilitySlot> findByDateBetween(LocalDate startDate, LocalDate endDate);

    long countByProviderIdAndIsBookedFalseAndIsBlockedFalse(String providerId);

    void deleteBySlotId(String slotId);
}