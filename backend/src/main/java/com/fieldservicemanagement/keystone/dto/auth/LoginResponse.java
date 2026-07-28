package com.fieldservicemanagement.keystone.dto.auth;

import com.fieldservicemanagement.keystone.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String username;
    private Role role;
    private Instant expiresAt;
}
