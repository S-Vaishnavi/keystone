package com.fieldservicemanagement.keystone.controller;

import com.fieldservicemanagement.keystone.service.PartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','TECHNICIAN')")
@Tag(name = "Parts", description = "Part management endpoints")
public class PartController {

    private final PartService partService;
}
