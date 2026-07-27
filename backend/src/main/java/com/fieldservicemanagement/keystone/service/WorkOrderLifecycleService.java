package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.workorder.AssignRequest;
import com.fieldservicemanagement.keystone.dto.workorder.StatusTransitionRequest;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;

import java.util.UUID;

public interface WorkOrderLifecycleService {

    WorkOrderResponse transitionStatus(UUID workOrderId, StatusTransitionRequest request, User actor);

    WorkOrderResponse assign(UUID workOrderId, AssignRequest request, User actor);
}