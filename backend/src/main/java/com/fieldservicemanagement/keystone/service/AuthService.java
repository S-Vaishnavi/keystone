package com.fieldservicemanagement.keystone.service;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.dto.auth.LoginRequest;
import com.fieldservicemanagement.keystone.dto.auth.LoginResponse;
import com.fieldservicemanagement.keystone.exception.ResourceNotFoundException;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        log.debug("Authentication attempt for email: {}", loginRequest.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + loginRequest.getEmail()));

        String token = jwtService.generateToken(user);

        log.debug("Authentication successful for email: {}, role: {}", user.getEmail(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .username(user.getEmail())
                .role(user.getRole())
                .expiresAt(jwtService.extractExpiration(token))
                .build();
    }
}
