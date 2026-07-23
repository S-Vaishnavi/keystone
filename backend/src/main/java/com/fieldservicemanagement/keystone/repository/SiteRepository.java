package com.fieldservicemanagement.keystone.repository;

import com.fieldservicemanagement.keystone.domain.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteRepository extends JpaRepository<Site, UUID> {

    Page<Site> findByCustomerId(UUID customerId, Pageable pageable);
}