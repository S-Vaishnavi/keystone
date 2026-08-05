package com.fieldservicemanagement.keystone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fieldservicemanagement.keystone.domain.enums.Role;
import com.fieldservicemanagement.keystone.dto.auth.LoginRequest;
import com.fieldservicemanagement.keystone.dto.auth.LoginResponse;
import com.fieldservicemanagement.keystone.exception.GlobalExceptionHandler;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.security.CustomUserDetailsService;
import com.fieldservicemanagement.keystone.security.JwtService;
import com.fieldservicemanagement.keystone.security.SecurityConfig;
import com.fieldservicemanagement.keystone.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "keystone.jwt.secret=bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlcw==",
        "keystone.jwt.expiration-ms=86400000"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void loginWithValidCredentials_shouldReturnJwtAndHttp200() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@keystone.io");
        loginRequest.setPassword("SecurePass123!");

        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwt-token-value")
                .username("admin@keystone.io")
                .role(Role.MANAGER)
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.username").value("admin@keystone.io"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @WithMockUser
    void loginWithWrongPassword_shouldReturnHttp401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@keystone.io");
        loginRequest.setPassword("WrongPassword!");

        when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
