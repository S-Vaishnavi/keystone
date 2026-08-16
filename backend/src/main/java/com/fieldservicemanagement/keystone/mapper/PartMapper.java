package com.fieldservicemanagement.keystone.mapper;

import com.fieldservicemanagement.keystone.domain.Part;
import com.fieldservicemanagement.keystone.dto.part.PartCreateRequest;
import com.fieldservicemanagement.keystone.dto.part.PartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Part toEntity(PartCreateRequest request);

    PartResponse toResponse(Part part);
}