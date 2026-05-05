package com.bridgelabz.scheduleservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockSlotRequest {

    @NotNull(message = "isBlocked is required")
    private Boolean isBlocked;
}