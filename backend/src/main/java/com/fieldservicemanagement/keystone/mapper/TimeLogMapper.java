package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.TimeLog;
import com.fieldservicemanagement.keystone.dto.timelog.TimeLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeLogMapper {

    @Mapping(target = "workOrderId", source = "workOrder.id")
    @Mapping(target = "workOrderCode", source = "workOrder.code")
    @Mapping(target = "technicianId", source = "technician.id")
    @Mapping(target = "technicianName", source = "technician.name")
    TimeLogResponse toResponse(TimeLog timeLog);

    List<TimeLogResponse> toResponseList(List<TimeLog> timeLogs);
}