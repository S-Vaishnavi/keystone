package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.dto.auth.LoginRequest;
import com.fieldservicemanagement.keystone.dto.auth.LoginResponse;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) { return null; }
}
