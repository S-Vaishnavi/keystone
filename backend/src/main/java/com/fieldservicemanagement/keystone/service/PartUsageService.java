package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.dto.part.PartUsageRequest;
import com.fieldservicemanagement.keystone.dto.part.PartUsageResponse;

import java.util.List;
import java.util.UUID;

public interface PartUsageService {

    PartUsageResponse logUsage(PartUsageRequest request);

    List<PartUsageResponse> getByWorkOrderId(UUID workOrderId);
}