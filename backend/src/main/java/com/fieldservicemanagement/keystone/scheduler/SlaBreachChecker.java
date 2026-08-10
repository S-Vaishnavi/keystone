package com.fieldservicemanagement.keystone.scheduler;

import com.fieldservicemanagement.keystone.domain.WorkOrder;
import com.fieldservicemanagement.keystone.service.SlaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SlaBreachChecker {

    private static final Logger logger = LoggerFactory.getLogger(SlaBreachChecker.class);

    private final SlaService slaService;

    @Scheduled(cron = "0 */15 * * * *")
    public void checkForBreaches() {
        logger.info("Scheduler started: SLA breach check");
        try {
            List<WorkOrder> breachedWorkOrders = slaService.scanForBreaches();
            if (breachedWorkOrders == null || breachedWorkOrders.isEmpty()) {
                logger.info("No SLA breaches were detected.");
            } else {
                logger.info("Number of breached work orders: {}", breachedWorkOrders.size());
                logger.warn("SLA breaches detected!");
                for (WorkOrder workOrder : breachedWorkOrders) {
                    logger.debug("Breached WorkOrder details - ID: {}, Code: {}", workOrder.getId(), workOrder.getCode());
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected scheduler failure during SLA breach check", e);
        }
    }
}
