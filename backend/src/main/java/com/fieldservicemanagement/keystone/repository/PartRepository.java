package com.fieldservicemanagement.keystone.repository;

import com.fieldservicemanagement.keystone.domain.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartRepository extends JpaRepository<Part, UUID> {

    Optional<Part> findBySku(String sku);
}