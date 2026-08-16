package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.part.PartCreateRequest;
import com.fieldservicemanagement.keystone.dto.part.PartResponse;
import com.fieldservicemanagement.keystone.dto.part.StockAdjustmentRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PartService {

    PartResponse create(PartCreateRequest request);

    PartResponse getById(UUID id);

    PageResponse<PartResponse> getAll(Pageable pageable);

    PartResponse adjustStock(UUID id, StockAdjustmentRequest request);
}