package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.dto.common.PageResponse;
import com.fieldservicemanagement.keystone.dto.site.SiteCreateRequest;
import com.fieldservicemanagement.keystone.dto.site.SiteResponse;
import com.fieldservicemanagement.keystone.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Create a new site")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<SiteResponse> create(
            @Valid 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Site creation payload")
            @RequestBody SiteCreateRequest request) {
        SiteResponse response = siteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a site by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SiteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    public ResponseEntity<SiteResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(siteService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get sites by customer ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    public ResponseEntity<PageResponse<SiteResponse>> getByCustomerId(
            @PathVariable UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(siteService.getByCustomerId(customerId, pageable));
    }
}