package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.workorder.AssignRequest;
import com.fieldservicemanagement.keystone.dto.workorder.StatusTransitionRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderCreateRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderSummary;
import com.fieldservicemanagement.keystone.service.WorkOrderLifecycleService;
import com.fieldservicemanagement.keystone.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
@Tag(name = "Work Orders", description = "Work order management endpoints")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderLifecycleService workOrderLifecycleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','MANAGER')")
    @Operation(summary = "Create a new work order")
    public ResponseEntity<WorkOrderResponse> create(
            @Valid @RequestBody WorkOrderCreateRequest request,
            @AuthenticationPrincipal User actor) {
        WorkOrderResponse response = workOrderService.createWorkOrder(request, actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','TECHNICIAN','CUSTOMER')")
    @Operation(summary = "Get all work orders")
    public ResponseEntity<PageResponse<WorkOrderSummary>> getAll(
            Pageable pageable,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(workOrderService.getWorkOrders(pageable, actor));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','TECHNICIAN','CUSTOMER')")
    @Operation(summary = "Get work order by ID")
    public ResponseEntity<WorkOrderResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id, actor));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    @Operation(summary = "Assign a technician to a work order")
    public ResponseEntity<WorkOrderResponse> assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRequest request,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(workOrderLifecycleService.assign(id, request, actor));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','TECHNICIAN')")
    @Operation(summary = "Transition work order status")
    public ResponseEntity<WorkOrderResponse> transitionStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusTransitionRequest request,
            @AuthenticationPrincipal User actor) {
        return ResponseEntity.ok(workOrderLifecycleService.transitionStatus(id, request, actor));
    }
}
