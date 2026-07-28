package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.Customer;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.customer.CustomerCreateRequest;
import com.fieldservicemanagement.keystone.dto.customer.CustomerResponse;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.CustomerMapper;
import com.fieldservicemanagement.keystone.repository.CustomerRepository;
import com.fieldservicemanagement.keystone.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> getAll(Pageable pageable) {
        Page<CustomerResponse> page = customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
        return PageResponse.from(page);
    }
}