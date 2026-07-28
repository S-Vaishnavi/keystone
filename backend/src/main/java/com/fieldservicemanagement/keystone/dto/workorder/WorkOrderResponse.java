package com.fieldservicemanagement.keystone.dto.workorder;

import com.fieldservicemanagement.keystone.domain.enums.Priority;
import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderResponse {

    private UUID id;
    private String code;
    private String title;
    private String description;
    private Priority priority;
    private WorkOrderStatus status;
    private LocalDateTime slaDueAt;

    private UUID customerId;
    private String customerName;

    private UUID siteId;
    private String siteName;

    private UUID assignedTechnicianId;
    private String assignedTechnicianName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    private List<WorkOrderStatusHistoryResponse> statusHistory;
}