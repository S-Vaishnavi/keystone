package com.fieldservicemanagement.keystone.dto.timelog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TimeLogCreateRequest {

    @NotNull(message = "Work order ID is required")
    private UUID workOrderId;

    @NotNull(message = "Minutes is required")
    @Min(value = 1, message = "Minutes must be at least 1")
    private Integer minutes;
}