package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogCreateRequest;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogResponse;
import com.fieldservicemanagement.keystone.service.TimeLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-logs")
@RequiredArgsConstructor
@Tag(name = "Time Logs", description = "Technician time logging endpoints")
public class TimeLogController {

    private final TimeLogService timeLogService;

    @PostMapping
    public ResponseEntity<TimeLogResponse> logTime(
            @Valid @RequestBody TimeLogCreateRequest request,
            @AuthenticationPrincipal User technician) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeLogService.logTime(request, technician));
    }

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<TimeLogResponse>> getByWorkOrderId(@PathVariable UUID workOrderId) {
        return ResponseEntity.ok(timeLogService.getByWorkOrderId(workOrderId));
    }
}