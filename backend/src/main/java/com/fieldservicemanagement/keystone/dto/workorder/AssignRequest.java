package com.fieldservicemanagement.keystone.dto.workorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignRequest {

    @NotNull(message = "Technician ID is required")
    private UUID technicianId;
}