package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.Site;
import com.fieldservicemanagement.keystone.dto.site.SiteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SiteMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    SiteResponse toResponse(Site site);
}