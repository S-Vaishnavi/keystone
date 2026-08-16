package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.site.SiteCreateRequest;
import com.fieldservicemanagement.keystone.dto.site.SiteResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SiteService {

    SiteResponse create(SiteCreateRequest request);

    SiteResponse getById(UUID id);

    PageResponse<SiteResponse> getByCustomerId(UUID customerId, Pageable pageable);
}