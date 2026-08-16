package com.fieldservicemanagement.keystone.security;

import com.fieldservicemanagement.keystone.controller.AuthController;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "keystone.jwt.secret=bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlcw==",
        "keystone.jwt.expiration-ms=86400000"
})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void protectedEndpointWithoutJwt_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/work-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicAuthEndpoint_shouldNotRequireAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertThat(status).isNotIn(401, 403);
    }

    @Test
    void swaggerUiEndpoint_shouldBePubliclyAccessible() throws Exception {
        MvcResult result = mockMvc.perform(get("/swagger-ui/index.html"))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertThat(status).isNotIn(401, 403);
    }

    @Test
    void apiDocsEndpoint_shouldBePubliclyAccessible() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertThat(status).isNotIn(401, 403);
    }
}
