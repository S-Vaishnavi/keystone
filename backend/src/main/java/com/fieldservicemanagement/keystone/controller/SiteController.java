package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.site.SiteCreateRequest;
import com.fieldservicemanagement.keystone.dto.site.SiteResponse;
import com.fieldservicemanagement.keystone.service.SiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
@Tag(name = "Sites", description = "Site management endpoints")
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody SiteCreateRequest request) {
        SiteResponse response = siteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(siteService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<SiteResponse>> getByCustomerId(
            @PathVariable UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(siteService.getByCustomerId(customerId, pageable));
    }
}