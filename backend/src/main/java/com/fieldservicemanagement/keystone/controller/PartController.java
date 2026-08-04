package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.part.PartCreateRequest;
import com.fieldservicemanagement.keystone.dto.part.PartResponse;
import com.fieldservicemanagement.keystone.dto.part.StockAdjustmentRequest;
import com.fieldservicemanagement.keystone.service.PartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@Tag(name = "Parts", description = "Parts inventory management endpoints")
public class PartController {

    private final PartService partService;

    @PostMapping
    public ResponseEntity<PartResponse> create(@Valid @RequestBody PartCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(partService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PartResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(partService.getAll(pageable));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<PartResponse> adjustStock(
            @PathVariable UUID id, @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(partService.adjustStock(id, request));
    }
}