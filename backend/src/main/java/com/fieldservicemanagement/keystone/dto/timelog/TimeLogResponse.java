package com.fieldservicemanagement.keystone.dto.timelog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogResponse {

    private UUID id;
    private UUID workOrderId;
    private String workOrderCode;
    private UUID technicianId;
    private String technicianName;
    private Integer minutes;
    private LocalDateTime loggedAt;
}