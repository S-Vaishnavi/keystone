package com.fieldservicemanagement.keystone.scheduler;

import com.fieldservicemanagement.keystone.service.SlaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlaBreachChecker {

    private static final Logger log = LoggerFactory.getLogger(SlaBreachChecker.class);

    private final SlaService slaService;

    @Scheduled(cron = "0 0 * * * *")
    public void checkSlaBreaches() {
        log.info("SLA breach scan started");
        try {
            slaService.scanForBreaches();
            log.info("SLA breach scan completed successfully");
        } catch (Exception e) {
            log.error("SLA breach scan failed", e);
        }
    }
}
