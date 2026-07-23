package com.fieldservicemanagement.keystone.dto.site;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SiteCreateRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Site name is required")
    @Size(max = 200, message = "Site name must not exceed 200 characters")
    private String name;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
}