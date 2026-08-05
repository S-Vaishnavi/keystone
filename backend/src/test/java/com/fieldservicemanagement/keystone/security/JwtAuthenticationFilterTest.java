package com.fieldservicemanagement.keystone.security;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_withNoAuthorizationHeader_shouldContinueFilterChainWithoutAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withNonBearerAuthorizationHeader_shouldContinueFilterChainWithoutAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withValidBearerToken_shouldSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(jwtService.extractUsername("valid-jwt-token")).thenReturn("user@keystone.io");
        UserDetails userDetails = User.builder()
                .username("user@keystone.io")
                .password("hashed")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")))
                .build();
        when(customUserDetailsService.loadUserByUsername("user@keystone.io")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-jwt-token")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("user@keystone.io");
    }

    @Test
    void doFilterInternal_withExpiredBearerToken_shouldNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-jwt-token");
        when(jwtService.extractUsername("expired-jwt-token")).thenReturn("user@keystone.io");
        UserDetails userDetails = User.builder()
                .username("user@keystone.io")
                .password("hashed")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")))
                .build();
        when(customUserDetailsService.loadUserByUsername("user@keystone.io")).thenReturn(userDetails);
        when(jwtService.isTokenValid("expired-jwt-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withInvalidSignatureToken_shouldNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-sig-token");
        when(jwtService.extractUsername("invalid-sig-token")).thenThrow(new SignatureException("Invalid signature"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
