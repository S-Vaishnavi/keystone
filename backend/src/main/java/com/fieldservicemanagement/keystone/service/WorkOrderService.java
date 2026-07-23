package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderCreateRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderSummary;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkOrderService {

    WorkOrderResponse createWorkOrder(WorkOrderCreateRequest request, User actor);

    WorkOrderResponse updateWorkOrder(UUID id, WorkOrderUpdateRequest request);

    PageResponse<WorkOrderSummary> getWorkOrders(Pageable pageable, User actor);

    WorkOrderResponse getWorkOrderById(UUID id, User actor);
}