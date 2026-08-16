package com.fieldservicemanagement.keystone.repository;

import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.domain.enums.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    Page<WorkOrder> findByAssignedTechnicianId(UUID technicianId, Pageable pageable);

    Page<WorkOrder> findByCustomerId(UUID customerId, Pageable pageable);

    Page<WorkOrder> findByStatus(WorkOrderStatus status, Pageable pageable);

    boolean existsByCode(String code);
    
    long countByStatus(WorkOrderStatus status);

    long countByStatusNotInAndSlaDueAtBefore(List<WorkOrderStatus> excludedStatuses, LocalDateTime now);
}