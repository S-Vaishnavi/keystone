package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import com.fieldservicemanagement.keystone.dto.report.DashboardSummaryResponse;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import com.fieldservicemanagement.keystone.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final WorkOrderRepository workOrderRepository;

    private static final List<WorkOrderStatus> TERMINAL_STATUSES =
            List.of(WorkOrderStatus.CLOSED, WorkOrderStatus.CANCELLED);

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        long newCount = workOrderRepository.countByStatus(WorkOrderStatus.NEW);
        long assignedCount = workOrderRepository.countByStatus(WorkOrderStatus.ASSIGNED);
        long inProgressCount = workOrderRepository.countByStatus(WorkOrderStatus.IN_PROGRESS);
        long onHoldCount = workOrderRepository.countByStatus(WorkOrderStatus.ON_HOLD);
        long completedCount = workOrderRepository.countByStatus(WorkOrderStatus.COMPLETED);
        long closedCount = workOrderRepository.countByStatus(WorkOrderStatus.CLOSED);
        long cancelledCount = workOrderRepository.countByStatus(WorkOrderStatus.CANCELLED);

        long total = newCount + assignedCount + inProgressCount + onHoldCount
                + completedCount + closedCount + cancelledCount;

        long slaBreached = workOrderRepository.countByStatusNotInAndSlaDueAtBefore(
                TERMINAL_STATUSES, LocalDateTime.now());

        return new DashboardSummaryResponse(
                total, newCount, assignedCount, inProgressCount,
                onHoldCount, completedCount, closedCount, cancelledCount, slaBreached
        );
    }
}