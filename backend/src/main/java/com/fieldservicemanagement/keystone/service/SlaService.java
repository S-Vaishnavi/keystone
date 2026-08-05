package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SlaService {

    private static final Logger log = LoggerFactory.getLogger(SlaService.class);

    public Instant computeDueDate(Priority priority, Instant createdAt) {
        log.debug("Computing SLA due date for priority={}, createdAt={}", priority, createdAt);
        return Instant.now();
    }

    public void scanForBreaches() {
        log.info("SLA breach scan started");
    }
}
