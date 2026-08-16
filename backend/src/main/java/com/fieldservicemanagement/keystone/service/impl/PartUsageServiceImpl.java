package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.Part;
import com.fieldservicemanagement.keystone.domain.PartUsage;
import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.dto.part.PartUsageRequest;
import com.fieldservicemanagement.keystone.dto.part.PartUsageResponse;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.PartUsageMapper;
import com.fieldservicemanagement.keystone.repository.PartRepository;
import com.fieldservicemanagement.keystone.repository.PartUsageRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import com.fieldservicemanagement.keystone.service.PartUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PartUsageServiceImpl implements PartUsageService {

    private final PartUsageRepository partUsageRepository;
    private final PartRepository partRepository;
    private final WorkOrderRepository workOrderRepository;
    private final PartUsageMapper partUsageMapper;

    @Override
    public PartUsageResponse logUsage(PartUsageRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work order not found with id: " + request.getWorkOrderId()));

        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Part not found with id: " + request.getPartId()));

        if (part.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for part " + part.getName()
                            + ". Available: " + part.getStockQuantity()
                            + ", requested: " + request.getQuantity());
        }

        part.setStockQuantity(part.getStockQuantity() - request.getQuantity());
        partRepository.saveAndFlush(part);

        PartUsage usage = PartUsage.builder()
                .workOrder(workOrder)
                .part(part)
                .quantity(request.getQuantity())
                .build();

        PartUsage saved = partUsageRepository.saveAndFlush(usage);
        return partUsageMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartUsageResponse> getByWorkOrderId(UUID workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException("Work order not found with id: " + workOrderId);
        }
        List<PartUsage> usages = partUsageRepository.findByWorkOrderId(workOrderId);
        return partUsageMapper.toResponseList(usages);
    }
}