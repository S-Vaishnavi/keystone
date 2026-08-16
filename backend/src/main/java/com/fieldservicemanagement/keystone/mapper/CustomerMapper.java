package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.Customer;
import com.fieldservicemanagement.keystone.dto.customer.CustomerCreateRequest;
import com.fieldservicemanagement.keystone.dto.customer.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerCreateRequest request);

    CustomerResponse toResponse(Customer customer);
}