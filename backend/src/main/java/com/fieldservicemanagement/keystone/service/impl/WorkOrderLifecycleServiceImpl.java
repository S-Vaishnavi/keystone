package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.domain.WorkOrderStatusHistory;
import com.fieldservicemanagement.keystone.domain.enums.Role;
import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import com.fieldservicemanagement.keystone.dto.workorder.AssignRequest;
import com.fieldservicemanagement.keystone.dto.workorder.StatusTransitionRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;
import com.fieldservicemanagement.keystone.exception.AccessDeniedException;
import com.fieldservicemanagement.keystone.exception.InvalidTransitionException;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.WorkOrderMapper;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderStatusHistoryRepository;
import com.fieldservicemanagement.keystone.service.WorkOrderLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderLifecycleServiceImpl implements WorkOrderLifecycleService {

    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(WorkOrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.NEW,
                EnumSet.of(WorkOrderStatus.ASSIGNED, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.ASSIGNED,
                EnumSet.of(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.IN_PROGRESS,
                EnumSet.of(WorkOrderStatus.ON_HOLD, WorkOrderStatus.COMPLETED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.ON_HOLD,
                EnumSet.of(WorkOrderStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.COMPLETED,
                EnumSet.of(WorkOrderStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.CLOSED,
                EnumSet.noneOf(WorkOrderStatus.class));
        ALLOWED_TRANSITIONS.put(WorkOrderStatus.CANCELLED,
                EnumSet.noneOf(WorkOrderStatus.class));
    }

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public WorkOrderResponse transitionStatus(UUID workOrderId, StatusTransitionRequest request, User actor) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + workOrderId));

        WorkOrderStatus currentStatus = workOrder.getStatus();
        WorkOrderStatus targetStatus = request.getToStatus();

        Set<WorkOrderStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.getOrDefault(
                currentStatus, EnumSet.noneOf(WorkOrderStatus.class));

        if (!allowedNextStatuses.contains(targetStatus)) {
            throw new InvalidTransitionException(
                    "Cannot transition work order from " + currentStatus + " to " + targetStatus);
        }

        checkTransitionAuthorization(workOrder, actor, targetStatus);

        WorkOrderStatusHistory history = WorkOrderStatusHistory.builder()
                .workOrder(workOrder)
                .fromStatus(currentStatus)
                .toStatus(targetStatus)
                .changedBy(actor)
                .note(request.getNote())
                .build();
        historyRepository.save(history);

        workOrder.setStatus(targetStatus);
        if (targetStatus == WorkOrderStatus.CLOSED) {
            workOrder.setClosedAt(java.time.LocalDateTime.now());
        }
        WorkOrder saved = workOrderRepository.save(workOrder);

        return workOrderMapper.toResponse(saved);
    }

    @Override
    public WorkOrderResponse assign(UUID workOrderId, AssignRequest request, User actor) {
        if (actor.getRole() != Role.DISPATCHER && actor.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Only dispatchers or managers can assign technicians");
        }

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + workOrderId));

        if (workOrder.getStatus() != WorkOrderStatus.NEW) {
            throw new InvalidTransitionException(
                    "Can only assign a technician while work order is NEW, current status: " + workOrder.getStatus());
        }

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Technician not found with id: " + request.getTechnicianId()));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException("User " + technician.getId() + " is not a technician");
        }

        workOrder.setAssignedTechnician(technician);

        WorkOrderStatusHistory history = WorkOrderStatusHistory.builder()
                .workOrder(workOrder)
                .fromStatus(WorkOrderStatus.NEW)
                .toStatus(WorkOrderStatus.ASSIGNED)
                .changedBy(actor)
                .note("Assigned to technician: " + technician.getName())
                .build();
        historyRepository.save(history);

        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        WorkOrder saved = workOrderRepository.save(workOrder);

        return workOrderMapper.toResponse(saved);
    }

    private void checkTransitionAuthorization(WorkOrder workOrder, User actor, WorkOrderStatus targetStatus) {
        boolean isOwningTechnician = workOrder.getAssignedTechnician() != null
                && workOrder.getAssignedTechnician().getId().equals(actor.getId());

        switch (actor.getRole()) {
            case MANAGER -> { /* managers can perform any valid transition */ }
            case DISPATCHER -> {
                if (targetStatus == WorkOrderStatus.CANCELLED) {
                    return;
                }
                throw new AccessDeniedException("Dispatchers cannot perform this status transition");
            }
            case TECHNICIAN -> {
                if (!isOwningTechnician) {
                    throw new AccessDeniedException("You are not assigned to this work order");
                }
            }
            case CUSTOMER -> throw new AccessDeniedException("Customers cannot change work order status");
        }
    }
}