package com.fieldservicemanagement.keystone.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private long totalWorkOrders;
    private long newCount;
    private long assignedCount;
    private long inProgressCount;
    private long onHoldCount;
    private long completedCount;
    private long closedCount;
    private long cancelledCount;
    private long slaBreachedCount;
}