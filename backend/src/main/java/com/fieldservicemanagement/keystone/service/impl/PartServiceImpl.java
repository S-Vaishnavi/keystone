package com.fieldservicemanagement.keystone.service.impl;

import com.fieldservicemanagement.keystone.domain.Part;
import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.part.PartCreateRequest;
import com.fieldservicemanagement.keystone.dto.part.PartResponse;
import com.fieldservicemanagement.keystone.dto.part.StockAdjustmentRequest;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.mapper.PartMapper;
import com.fieldservicemanagement.keystone.repository.PartRepository;
import com.fieldservicemanagement.keystone.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final PartMapper partMapper;

    @Override
    public PartResponse create(PartCreateRequest request) {
        if (partRepository.findBySku(request.getSku()).isPresent()) {
            throw new IllegalArgumentException("A part with SKU " + request.getSku() + " already exists");
        }
        Part part = partMapper.toEntity(request);
        Part saved = partRepository.saveAndFlush(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PartResponse getById(UUID id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));
        return partMapper.toResponse(part);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PartResponse> getAll(Pageable pageable) {
        Page<PartResponse> page = partRepository.findAll(pageable)
                .map(partMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public PartResponse adjustStock(UUID id, StockAdjustmentRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));

        int newQuantity = part.getStockQuantity() + request.getQuantityChange();
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Cannot reduce stock below zero. Current: " + part.getStockQuantity()
                            + ", requested change: " + request.getQuantityChange());
        }
        part.setStockQuantity(newQuantity);
        Part saved = partRepository.saveAndFlush(part);
        return partMapper.toResponse(saved);
    }
}