package com.fieldservicemanagement.keystone.dto.workorder;

import com.fieldservicemanagement.keystone.domain.enums.Priority;
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
public class WorkOrderSummary {

    private UUID id;
    private String code;
    private String title;
    private Priority priority;
    private WorkOrderStatus status;
    private LocalDateTime slaDueAt;
    private String customerName;
    private String siteName;
    private String assignedTechnicianName;
}