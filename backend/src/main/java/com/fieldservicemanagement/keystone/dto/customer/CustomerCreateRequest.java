package com.fieldservicemanagement.keystone.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    private String name;

    @Size(max = 500, message = "Contact info must not exceed 500 characters")
    private String contactInfo;
}