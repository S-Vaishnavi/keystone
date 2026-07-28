package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.domain.WorkOrderStatusHistory;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderStatusHistoryResponse;
import com.fieldservicemanagement.keystone.dto.workorder.WorkOrderSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "siteId", source = "site.id")
    @Mapping(target = "siteName", source = "site.name")
    @Mapping(target = "assignedTechnicianId", source = "assignedTechnician.id")
    @Mapping(target = "assignedTechnicianName", source = "assignedTechnician.name")
    @Mapping(target = "statusHistory", ignore = true)
    WorkOrderResponse toResponse(WorkOrder workOrder);

    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "siteName", source = "site.name")
    @Mapping(target = "assignedTechnicianName", source = "assignedTechnician.name")
    WorkOrderSummary toSummary(WorkOrder workOrder);

    @Mapping(target = "changedById", source = "changedBy.id")
    @Mapping(target = "changedByName", source = "changedBy.name")
    WorkOrderStatusHistoryResponse toHistoryResponse(WorkOrderStatusHistory history);

    List<WorkOrderStatusHistoryResponse> toHistoryResponseList(List<WorkOrderStatusHistory> historyList);
}