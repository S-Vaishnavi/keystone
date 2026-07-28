package com.fieldservicemanagement.keystone.dto.workorder;

import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusTransitionRequest {

    @NotNull(message = "Target status is required")
    private WorkOrderStatus toStatus;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}