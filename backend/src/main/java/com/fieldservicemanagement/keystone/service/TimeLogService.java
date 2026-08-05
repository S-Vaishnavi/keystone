package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogCreateRequest;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogResponse;

import java.util.List;
import java.util.UUID;

public interface TimeLogService {

    TimeLogResponse logTime(TimeLogCreateRequest request, User technician);

    List<TimeLogResponse> getByWorkOrderId(UUID workOrderId);
}