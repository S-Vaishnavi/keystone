package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlaService {

    private static final Logger log = LoggerFactory.getLogger(SlaService.class);

    private final WorkOrderRepository workOrderRepository;

    @Transactional
    public void processBreachedWorkOrders() {}

    public boolean isSlaBreached(WorkOrder workOrder) { return false; }

    public List<WorkOrder> findAllBreachedWorkOrders() { return null; }
}
