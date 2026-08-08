package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.domain.enums.Priority;
import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlaService {

    private final WorkOrderRepository workOrderRepository;

    public Instant computeDueDate(Priority priority, Instant createdAt) {
        if (priority == null || createdAt == null) {
            return null;
        }

        return switch (priority) {
            case CRITICAL -> createdAt.plus(Duration.ofHours(4));
            case HIGH -> createdAt.plus(Duration.ofHours(8));
            case MEDIUM -> createdAt.plus(Duration.ofHours(24));
            case LOW -> createdAt.plus(Duration.ofHours(72));
        };
    }

    public List<WorkOrder> scanForBreaches() {
        Instant now = Instant.now();
        List<WorkOrder> activeWorkOrders = workOrderRepository.findAll().stream()
                .filter(this::isActive)
                .collect(Collectors.toList());

        return activeWorkOrders.stream()
                .filter(wo -> isOverdue(wo, now))
                .collect(Collectors.toList());
    }

    private boolean isActive(WorkOrder workOrder) {
        WorkOrderStatus status = workOrder.getStatus();
        return status != null && status != WorkOrderStatus.COMPLETED && status != WorkOrderStatus.CANCELLED;
    }

    private boolean isOverdue(WorkOrder workOrder, Instant now) {
        Instant dueDate = computeDueDate(workOrder.getPriority(), workOrder.getCreatedAt());
        return dueDate != null && now.isAfter(dueDate);
    }
}
