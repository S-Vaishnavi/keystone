package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.Customer;
import com.fieldservicemanagement.keystone.domain.Site;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.site.SiteCreateRequest;
import com.fieldservicemanagement.keystone.dto.site.SiteResponse;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.SiteMapper;
import com.fieldservicemanagement.keystone.repository.CustomerRepository;
import com.fieldservicemanagement.keystone.repository.SiteRepository;
import com.fieldservicemanagement.keystone.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;
    private final SiteMapper siteMapper;

    @Override
    public SiteResponse create(SiteCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        Site site = Site.builder()
                .customer(customer)
                .name(request.getName())
                .address(request.getAddress())
                .build();

        Site saved = siteRepository.save(site);
        return siteMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteResponse getById(UUID id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + id));
        return siteMapper.toResponse(site);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SiteResponse> getByCustomerId(UUID customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        Page<SiteResponse> page = siteRepository.findByCustomerId(customerId, pageable)
                .map(siteMapper::toResponse);
        return PageResponse.from(page);
    }
}