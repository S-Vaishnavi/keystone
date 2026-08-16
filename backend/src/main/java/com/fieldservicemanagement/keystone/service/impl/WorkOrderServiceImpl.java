package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.Customer;
import com.fieldservicemanagement.keystone.domain.Site;
import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.domain.enums.Priority;
import com.fieldservicemanagement.keystone.domain.enums.Role;
import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderCreateRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderSummary;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderUpdateRequest;
import com.fieldservicemanagement.keystone.exception.AccessDeniedException;
import com.fieldservicemanagement.keystone.exception.InvalidTransitionException;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.WorkOrderMapper;
import com.fieldservicemanagement.keystone.repository.CustomerRepository;
import com.fieldservicemanagement.keystone.repository.SiteRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderStatusHistoryRepository;
import com.fieldservicemanagement.keystone.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final WorkOrderStatusHistoryRepository historyRepository;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public WorkOrderResponse createWorkOrder(WorkOrderCreateRequest request, User actor) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Site not found with id: " + request.getSiteId()));

        if (!site.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Site does not belong to the specified customer");
        }

        WorkOrder workOrder = WorkOrder.builder()
                .code(generateWorkOrderCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(WorkOrderStatus.NEW)
                .slaDueAt(calculateSlaDueAt(request.getPriority()))
                .customer(customer)
                .site(site)
                .build();

        //WorkOrder saved = workOrderRepository.save(workOrder);
        WorkOrder saved = workOrderRepository.saveAndFlush(workOrder);
        return workOrderMapper.toResponse(saved);
    }

    @Override
    public WorkOrderResponse updateWorkOrder(UUID id, WorkOrderUpdateRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + id));

        if (workOrder.getStatus() == WorkOrderStatus.CLOSED
                || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new InvalidTransitionException(
                    "Cannot update a work order that is " + workOrder.getStatus());
        }

        if (request.getTitle() != null) {
            workOrder.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workOrder.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            workOrder.setPriority(request.getPriority());
        }

        WorkOrder saved = workOrderRepository.save(workOrder);
        return workOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkOrderSummary> getWorkOrders(Pageable pageable, User actor) {
        Page<WorkOrder> page = switch (actor.getRole()) {
            case TECHNICIAN -> workOrderRepository.findByAssignedTechnicianId(actor.getId(), pageable);
            case CUSTOMER -> workOrderRepository.findByCustomerId(actor.getId(), pageable);
            case DISPATCHER, MANAGER -> workOrderRepository.findAll(pageable);
        };
        return PageResponse.from(page.map(workOrderMapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(UUID id, User actor) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + id));

        checkAccessScope(workOrder, actor);

        WorkOrderResponse response = workOrderMapper.toResponse(workOrder);
        var history = historyRepository.findByWorkOrderIdOrderByChangedAtAsc(id);
        response.setStatusHistory(workOrderMapper.toHistoryResponseList(history));
        return response;
    }

    private void checkAccessScope(WorkOrder workOrder, User actor) {
        boolean allowed = switch (actor.getRole()) {
            case MANAGER, DISPATCHER -> true;
            case TECHNICIAN -> workOrder.getAssignedTechnician() != null
                    && workOrder.getAssignedTechnician().getId().equals(actor.getId());
            case CUSTOMER -> workOrder.getCustomer().getId().equals(actor.getId());
        };
        if (!allowed) {
            throw new AccessDeniedException("You do not have access to this work order");
        }
    }

    private String generateWorkOrderCode() {
        long count = workOrderRepository.count() + 1;
        return String.format("WO-%d-%06d", LocalDateTime.now().getYear(), count);
    }

    private LocalDateTime calculateSlaDueAt(Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case CRITICAL -> now.plusHours(4);
            case HIGH -> now.plusHours(24);
            case MEDIUM -> now.plusHours(72);
            case LOW -> now.plusHours(168);
        };
    }
}