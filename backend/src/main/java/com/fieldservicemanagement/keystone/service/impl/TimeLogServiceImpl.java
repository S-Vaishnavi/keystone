package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.TimeLog;
import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogCreateRequest;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogResponse;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.TimeLogMapper;
import com.fieldservicemanagement.keystone.repository.TimeLogRepository;
import com.fieldservicemanagement.keystone.repository.WorkOrderRepository;
import com.fieldservicemanagement.keystone.service.TimeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeLogServiceImpl implements TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final WorkOrderRepository workOrderRepository;
    private final TimeLogMapper timeLogMapper;

    @Override
    public TimeLogResponse logTime(TimeLogCreateRequest request, User technician) {
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Work order not found with id: " + request.getWorkOrderId()));

        TimeLog timeLog = TimeLog.builder()
                .workOrder(workOrder)
                .technician(technician)
                .minutes(request.getMinutes())
                .build();

        TimeLog saved = timeLogRepository.saveAndFlush(timeLog);
        return timeLogMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeLogResponse> getByWorkOrderId(UUID workOrderId) {
        if (!workOrderRepository.existsById(workOrderId)) {
            throw new ResourceNotFoundException("Work order not found with id: " + workOrderId);
        }
        List<TimeLog> logs = timeLogRepository.findByWorkOrderId(workOrderId);
        return timeLogMapper.toResponseList(logs);
    }
}