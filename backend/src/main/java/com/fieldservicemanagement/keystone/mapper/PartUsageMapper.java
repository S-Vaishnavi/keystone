package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.PartUsage;
import com.fieldservicemanagement.keystone.dto.part.PartUsageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartUsageMapper {

    @Mapping(target = "workOrderId", source = "workOrder.id")
    @Mapping(target = "workOrderCode", source = "workOrder.code")
    @Mapping(target = "partId", source = "part.id")
    @Mapping(target = "partName", source = "part.name")
    @Mapping(target = "partSku", source = "part.sku")
    PartUsageResponse toResponse(PartUsage partUsage);

    List<PartUsageResponse> toResponseList(List<PartUsage> partUsages);
}