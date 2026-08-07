package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.dto.part.PartUsageRequest;
import com.fieldservicemanagement.keystone.dto.part.PartUsageResponse;
import com.fieldservicemanagement.keystone.service.PartUsageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/part-usage")
@RequiredArgsConstructor
@Tag(name = "Part Usage", description = "Track parts consumed per work order")
public class PartUsageController {

    private final PartUsageService partUsageService;

    @PostMapping
    public ResponseEntity<PartUsageResponse> logUsage(@Valid @RequestBody PartUsageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partUsageService.logUsage(request));
    }

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<PartUsageResponse>> getByWorkOrderId(@PathVariable UUID workOrderId) {
        return ResponseEntity.ok(partUsageService.getByWorkOrderId(workOrderId));
    }
}