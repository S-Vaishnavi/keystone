package com.fieldservicemanagement.keystone.dto.part;

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
public class PartUsageResponse {

    private UUID id;
    private UUID workOrderId;
    private String workOrderCode;
    private UUID partId;
    private String partName;
    private String partSku;
    private Integer quantity;
    private LocalDateTime loggedAt;
}