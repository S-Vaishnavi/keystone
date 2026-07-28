package com.fieldservicemanagement.keystone.repository;

import com.fieldservicemanagement.keystone.domain.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TimeLogRepository extends JpaRepository<TimeLog, UUID> {

    List<TimeLog> findByWorkOrderId(UUID workOrderId);

    List<TimeLog> findByTechnicianId(UUID technicianId);
}