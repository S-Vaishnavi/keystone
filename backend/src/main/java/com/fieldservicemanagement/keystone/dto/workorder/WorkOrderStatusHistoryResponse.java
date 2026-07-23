package com.fieldservicemanagement.keystone.dto.workorder;

import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderStatusHistoryResponse {

    private UUID id;
    private WorkOrderStatus fromStatus;
    private WorkOrderStatus toStatus;
    private UUID changedById;
    private String changedByName;
    private String note;
    private LocalDateTime changedAt;
}