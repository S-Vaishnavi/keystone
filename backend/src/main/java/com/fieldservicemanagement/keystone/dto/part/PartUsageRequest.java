package com.fieldservicemanagement.keystone.dto.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PartUsageRequest {

    @NotNull(message = "Work order ID is required")
    private UUID workOrderId;

    @NotNull(message = "Part ID is required")
    private UUID partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}