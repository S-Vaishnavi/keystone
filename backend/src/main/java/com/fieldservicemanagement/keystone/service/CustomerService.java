package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.dto.customer.CustomerCreateRequest;
import com.fieldservicemanagement.keystone.dto.customer.CustomerResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);

    CustomerResponse getById(UUID id);

    PageResponse<CustomerResponse> getAll(Pageable pageable);
}