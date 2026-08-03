package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.service.TimeLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/time-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TECHNICIAN')")
@Tag(name = "Time Logs", description = "Time log management endpoints")
public class TimeLogController {

    private final TimeLogService timeLogService;
}
