package com.fieldservicemanagement.keystone.dto.auth;

import com.fieldservicemanagement.keystone.domain.enums.Role;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private String message;
    private Instant createdAt;
}
